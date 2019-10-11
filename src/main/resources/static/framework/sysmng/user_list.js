var userList={
    contextRoot:"/",
    width:600,
    height:400
};
//初始化
userList.init=function(ctxRoot,gridWidth,gridHeight){
	this.contextRoot = ctxRoot;
	this.width = gridWidth;
    this.height = gridHeight;
	this.initUserList();
	this.initDetailWin();
	this.initForm();
};
//列表
userList.initUserList=function(){
	$('#userList').datagrid({
		url:'usermng.do?action=list',
		loadMsg:'数据加载中...',
		width : userList.width,
		height : userList.height,
		nowrap : false,
		striped : true,
		collapsible:false,
        fitColumns:true,
		sortName : 'loginOrder',
		sortOrder : 'desc',
		remoteSort : true,
		pageSize : 20,
		idField : 'id',
		frozenColumns:[[
   		    {field:'id',checkbox:true}
   		]],
		columns:[[
            {field:'loginName',title:'登录名',sortable:true,width:120,halign:'center'},
            {field:'userName',title:'用户名',sortable:true,width:120,halign:'center'},
            {field:'mailAddr',title:'邮箱',sortable:true,width:180,halign:'center'},
            {field:'phoneNum',title:'电话',sortable:true,width:120,halign:'center'},
            {field:'sysDept',title:'部门',sortable:false,width:120,halign:'center',formatter:function(value,row,index){
            	return value.deptName;
            }},
            {field:'loginState',title:'状态',sortable:true,width:80,halign:'center',formatter:function(value,row,index){
            	if(value=="0"){
            		return "<span style='color:green;'>启用</span>";
            	}else if(value=="1"){
            		return "<span style='color:red;'>禁用</span>";
            	}
            }},
            {field:'loginRoles',title:'角色',sortable:false,width:150,halign:'center',formatter:function(value,row,index){
            	var roleStr="";
            	for(var i=0;i<value.length;i++){
            		roleStr+=value[i].roleName+"<br/>";
            	}
            	return roleStr;
            }}
		]],
		pagination:true,
		rownumbers:true,
		onDblClickRow:function(rowIndex, row){
			userList.showDetail(row.id);
		},
		toolbar:[{
			text:'增加',
			iconCls:'icon-add',
			handler:function(){
				userList.showDetail();
			}
		},{
			text:'编辑',
			iconCls:'icon-edit',
			handler:function(){
				var rec=$("#userList").datagrid("getSelections");
				if(rec.length==0){
					$.messager.alert('提示','请选择记录','info');
				}else{
					userList.showDetail(rec[0].id);
				}
			}
		},{
			text:'删除',
			iconCls:'icon-remove',
			handler:function(){
				var rec=$("#userList").datagrid("getSelections");
				var count=rec.length;
				var ids="";
				if(count==0){
					$.messager.alert('提示','请选择记录','info');
                    return;
				}
                $.messager.confirm("提示","删除选中的"+count+"行记录吗?",function(r){
                    if(r){
                        for(var i=0;i<count;i++){
                            ids+=(i != count-1) ? rec[i].id+"," : rec[i].id ;
                        }
                        $.post("usermng.do?action=remove",{"ids":ids},function(data){
                            var gd=$("#userList");
                            gd.datagrid("clearSelections");
                            gd.datagrid("reload");
                        });
                    }
                });
			}
		},'-',{
			iconCls: 'icon-top',
			text:"置顶",
			handler: function(){
				var rec=$('#userList').datagrid('getSelected');
				if(rec==null){
					$.messager.alert('提示','请选择记录','info');
				}else{
					userList.moveOrder(rec.id,'top');				
				}
			}
		},{
			iconCls: 'icon-up',
			text:"上移",
			handler: function(){
				var rec=$('#userList').datagrid('getSelected');
				if(rec==null){
					$.messager.alert('提示','请选择记录','info');
				}else{
					userList.moveOrder(rec.id,'up');				
				}
			}
		},{
			iconCls: 'icon-down',
			text:"下移",
			handler: function(){
				var rec=$('#userList').datagrid('getSelected');
				if(rec==null){
					$.messager.alert('提示','请选择记录','info');
				}else{
					userList.moveOrder(rec.id,'down');				
				}
			}
		},{
			iconCls: 'icon-bottom',
			text:"置底",
			handler: function(){
				var rec=$('#userList').datagrid('getSelected');
				if(rec==null){
					$.messager.alert('提示','请选择记录','info');
				}else{
					userList.moveOrder(rec.id,'bottom');				
				}
			}
		}]
	});
};
//表单窗口
userList.initDetailWin=function(){
	$('#detailWin').window({
		width:600,  
		height:400,
		top:20,
		modal:true,
		title:'编辑',
		iconCls:'icon-edit',
		collapsible:false,
		minimizable:false,
		maximizable:false,
		resizable:false,
		closable:true,
		closed:true,
        onMove:function(left,top){
            if(top < 0){
                $('#detailWin').window('move',{'left':left,'top':0});
            }
        }
	});
};

//表单
userList.initForm=function(){
	//验证
	$("#ff input[name='loginName']").validatebox({  
	   required: true,  
	   validType: 'length[1,30]',
	   missingMessage:'必填',
	   invalidMessage:'30字内'	   
	});
    $("#ff input[name='mailAddr']").validatebox({
        validType: 'email'
    });
	$("#ff input[name='userName']").validatebox({
	   required: true,  
	   validType: 'length[1,30]',
	   missingMessage:'必填',
	   invalidMessage:'30字内'	   
	});
	$("#deptId").combotree({
		url:'deptmng.do?action=showTree'
	});
	
};
//保存
userList.save=function(){
	$('#ff').form('submit',{
		  url:'usermng.do?action=save',  
		  onSubmit: function(){
			 var ok="false";
			 if($('#ff').form('validate')){
				 ok = $.ajax({
					  type: "POST",
					  url: "usermng.do?action=checkLoginName",
					  data:{loginName:$("#ff input[name='loginName']").val(),loginId:$("#ff input[name='id']").val()},
					  async: false
				 }).responseText;
			 }
			 if("true"==ok){
				 $.messager.progress();
				 return true;
			 }else{
				 $.messager.alert('错误','登录名已经存在','error');
				 return false;
			 }
		  },  
		  success:function(responseText){
			 $.messager.progress('close');
			 $('#detailWin').window('close');			 
			 $('#userList').datagrid('reload');
			 $.messager.show({
				 	title:'提示',
				    msg:'保存成功!',
					timeout:8000,
					showType:'slide'
		     });
		  }  
	});
};
//展示表单
userList.showDetail=function(id){
	$('#ff').form('clear');
	if(id){
		$.post("usermng.do?action=show",{"id":id},function(data){
			$("#deptId").combotree('clear');
			$('#ff').form('load',data);
			if(data.sysDept.id){
				$("#deptId").combotree('setValue',data.sysDept.id);
				$("#deptId").combotree('setText',data.sysDept.deptName);
			}
			$("input[name='loginRole']").each(function(){
				this.checked=false;
				for(var i=0;i<data.loginRoles.length;i++){
					if(data.loginRoles[i].id==this.value){
						this.checked=true;
						break;
					}
				}
			});
		});
	}else{
		$("#ff select[name='loginState']").val('0');
	}
	$('#detailWin').window('open');
};
//调整顺序
userList.moveOrder=function(id,direct){
	$.post('usermng.do?action=moveOrder',{'id':id,'direct':direct},function(data){
		 $('#userList').datagrid('reload');
	});
};
//查询
userList.query=function(){
	$("#userList").datagrid("clearSelections");
	$('#userList').datagrid('load',{'loginName':$("#queryForm input[name='loginName']").val(),'userName':$("#queryForm input[name='userName']").val(),'loginState':$("#queryForm select[name='loginState']").val()});
};
//重置
userList.resetQuery=function(){
	$("#queryForm input[name='loginName']").val("");
	$("#queryForm input[name='userName']").val("");
	$("#queryForm select[name='loginState']").val("-1");
	userList.query();
};


