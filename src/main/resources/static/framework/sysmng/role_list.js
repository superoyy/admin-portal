var roleList={
    contextRoot:"/",
    width:600,
    height:400,
    currentRoleId:"",
    permitTreeStatus:1
};
//初始化
roleList.init=function(ctxRoot,gridWidth,gridHeight){
	this.contextRoot=ctxRoot;
	this.width=gridWidth;
    this.height=gridHeight;
	this.initRoleList();
	this.initDetailWin();
	this.initForm();
	this.initMembList();
	this.initMembWin();
	this.initPermitTree();
	this.initPermitWin();
};
//列表
roleList.initRoleList=function(){
	$('#roleList').datagrid({
		url:'rolemng.do?action=list',
		loadMsg:'数据加载中...',
		width:roleList.width,
		height:roleList.height,
		nowrap: false,
		striped: true,
		collapsible:false,
        fitColumns:true,
		sortName: 'roleOrder',
		sortOrder: 'desc',
		remoteSort: true,
		pageSize:20,
		idField:'id',
		frozenColumns:[[
   		    {field:'id',checkbox:true}
   		]],
		columns:[[
            {field:'roleName',title:'名称',sortable:true,width:120,halign:'center'},
            {field:'roleState',title:'状态',sortable:true,width:80,halign:'center',formatter:function(value,row,index){
            	if(value=="0"){
            		return "<span style='color:green;'>启用</span>";
            	}else if(value=="1"){
            		return "<span style='color:red;'>禁用</span>";
            	}
            }},
            {field:'membCount',title:'成员数',sortable:false,width:80,halign:'center',formatter:function(value,row,index){
            	return "<a href=\"javascript:roleList.showMemb('"+row.id+"','"+row.roleName+"')\">"+value+"</a>";
            }},
            {field:'permitCount',title:'功能和功能点权限',sortable:false,width:150,halign:'center',formatter:function(value,row,index){
            	return "<a href=\"javascript:roleList.showPermit('"+row.id+"','"+row.roleName+"')\">"+value+"</a>";
            }},
            {field:'roleDesc',title:'说明',sortable:true,width:400,halign:'center'}
		]],
		pagination:true,
		rownumbers:true,
		onDblClickRow:function(rowIndex, row){
			roleList.showDetail(row.id);
		},
		toolbar:[{
			text:'增加',
			iconCls:'icon-add',
			handler:function(){
				roleList.showDetail();
			}
		},{
			text:'编辑',
			iconCls:'icon-edit',
			handler:function(){
				var rec=$("#roleList").datagrid("getSelections");
				if(rec.length==0){
					$.messager.alert('提示','请选择记录','info');
				}else{
					roleList.showDetail(rec[0].id);
				}
			}
		},{
			text:'删除',
			iconCls:'icon-remove',
			handler:function(){
				var rec=$("#roleList").datagrid("getSelections");
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
                        $.post("rolemng.do?action=remove",{"ids":ids},function(data){
                            var gd=$("#roleList");
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
				var rec=$('#roleList').datagrid('getSelected');
				if(rec==null){
					$.messager.alert('提示','请选择记录','info');
				}else{
					roleList.moveOrder(rec.id,'top');				
				}
			}
		},{
			iconCls: 'icon-up',
			text:"上移",
			handler: function(){
				var rec=$('#roleList').datagrid('getSelected');
				if(rec==null){
					$.messager.alert('提示','请选择记录','info');
				}else{
					roleList.moveOrder(rec.id,'up');				
				}
			}
		},{
			iconCls: 'icon-down',
			text:"下移",
			handler: function(){
				var rec=$('#roleList').datagrid('getSelected');
				if(rec==null){
					$.messager.alert('提示','请选择记录','info');
				}else{
					roleList.moveOrder(rec.id,'down');				
				}
			}
		},{
			iconCls: 'icon-bottom',
			text:"置底",
			handler: function(){
				var rec=$('#roleList').datagrid('getSelected');
				if(rec==null){
					$.messager.alert('提示','请选择记录','info');
				}else{
					roleList.moveOrder(rec.id,'bottom');				
				}
			}
		}]
	});
};
//表单窗口
roleList.initDetailWin=function(){
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
roleList.initForm=function(){
	//验证
	$("#ff input[name='roleName']").validatebox({  
	   required: true,  
	   validType: 'length[1,30]',
	   missingMessage:'必填',
	   invalidMessage:'30字内'	   
	});
	$("#ff textarea[name='roleDesc']").validatebox({  
	   validType: 'length[0,300]',
	   invalidMessage:'300字内'	   
	}); 
};
//成员窗口
roleList.initMembWin=function(){
	$('#membWin').window({
		width:600,  
		height:400,
		top:20,
        left:200,
		modal:true,
		title:'角色成员',
		iconCls:'icon-edit',
		collapsible:false,
		minimizable:false,
		maximizable:false,
		resizable:false,
		closable:true,
		closed:true,
		onClose:function(){
			 $('#roleList').datagrid('reload');		 
		}
	});
};
//成员列表
roleList.initMembList=function(){
	$('#membList').datagrid({
		url:'rolemng.do?action=membList',
		loadMsg:'数据加载中...',
		width:583,
		height:360,
		nowrap: false,
		striped: true,
		collapsible:false,
		sortName: 'loginOrder',
		sortOrder: 'asc',
		remoteSort: true,
		pageSize:20,
		idField:'id',
		columns:[[
            {field:'loginName',title:'登录名',sortable:true,width:185,align:'center'},
            {field:'userName',title:'用户名',sortable:true,width:185,align:'center'},
            {field:'loginState',title:'状态',sortable:true,width:80,align:'center',formatter:function(value,row,index){
            	if(value=="0"){
            		return "<span style='color:green;'>启用</span>";
            	}else if(value=="1"){
            		return "<span style='color:red;'>禁用</span>";
            	}
            }},
		    {field:'isMemb',title:'是/否成员',sortable:true,width:100,align:'center',formatter:function(value,row,index){
		    	if(value){
			    	return "<input id='memb_"+row.id+"' value='"+row.id+"' type='checkbox' checked onclick='roleList.updateMemb(this)' />";
		    	}else{
			    	return "<input id='memb_"+row.id+"' value='"+row.id+"' type='checkbox' onclick='roleList.updateMemb(this)' />";
		    	}
            }}
		]],
		pagination:true,
		rownumbers:true
	});
};
//权限窗口
roleList.initPermitWin=function(){
	$('#permitWin').window({
		width:300,  
		height:540,
		top:5,
		modal:true,
		title:'角色权限',
		iconCls:'icon-edit',
		collapsible:false,
		minimizable:false,
		maximizable:false,
		resizable:false,
		closable:true,
		closed:true,
		onClose:function(){
			 $('#roleList').datagrid('reload');		 
		}
	});
};
//权限树
roleList.initPermitTree=function(){
	$("#permitTree").tree({
		checkbox:true,
		animate:true,
		cascadeCheck:false,
		lines:true,
		onCheck:function(node,checked){
			if(roleList.permitTreeStatus==0){
				roleList.permitTreeStatus=1;
				if(node.attributes.type=="module"){
					roleList.updatePermit(node.attributes.id,checked);
				}else if(node.attributes.type=="func"){
					roleList.updatePermitFunc(node.attributes.id,checked);
				}
				if(checked){
					//选中全部上级节点
					roleList.checkUpNode(node);
				}else{
					//取消全部下级节点选中状态
					roleList.unCheckSubNode(node);
				}
				roleList.permitTreeStatus=0;
			}
		}
	});
};
//选中上级节点
roleList.checkUpNode=function(node){
	var upNode=$("#permitTree").tree('getParent',node.target);
	if(upNode){
		$("#permitTree").tree('check',upNode.target);
		roleList.checkUpNode(upNode);
	}
};
//取消选中下级节点
roleList.unCheckSubNode=function(node){
	var subNodes=$("#permitTree").tree('getChildren',node.target);
	if(subNodes && subNodes.length!=0){
		for(var i=0;i<subNodes.length;i++){
			$("#permitTree").tree('uncheck',subNodes[i].target);
			roleList.unCheckSubNode(subNodes[i]);
		}
	}
};
//选择成员
roleList.updateMemb=function(checkbox){
	$.post("rolemng.do?action=updateMemb",{roleId:this.currentRoleId,loginId:checkbox.value,isMemb:checkbox.checked},function(data){
		 $.messager.show({
		 	title:'提示',
		    msg:'成功更新成员',
			timeout:1000,
			showType:'slide'
	     });
	});
};
//更新权限
roleList.updatePermit=function(moduleId,checked){
	$.post("rolemng.do?action=updatePermit",{"roleId":this.currentRoleId,"moduleId":moduleId,"checked":checked},function(data){
		 $.messager.show({
		 	title:'提示',
		    msg:'成功更新模块权限',
			timeout:2000,
			showType:'slide'
	     });
	});
};
//更新功能点权限
roleList.updatePermitFunc=function(funcId,checked){
	$.post("rolemng.do?action=updatePermitFunc",{"roleId":this.currentRoleId,"funcId":funcId,"checked":checked},function(data){
		 $.messager.show({
		 	title:'提示',
		    msg:'成功更新功能项权限',
			timeout:2000,
			showType:'slide'
	     });
	});
};
//保存
roleList.save=function(){
	$('#ff').form('submit',{
		  url:'rolemng.do?action=save',  
		  onSubmit: function(){
			 if($('#ff').form('validate')){
				 $.messager.progress();
				 return true;
			 }
			 return false;
		  },  
		  success:function(responseText){
			 $.messager.progress('close');
			 $('#detailWin').window('close');			 
			 $('#roleList').datagrid('reload');
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
roleList.showDetail=function(id){
	$('#ff').form('clear');
	if(id){
		$.post("rolemng.do?action=show",{"id":id},function(data){
			$('#ff').form('load',data);
		});
	}else{
		$("#ff select[name='roleState']").val('0');
	}
	$('#detailWin').window('open');
};
//展示成员
roleList.showMemb=function(id,roleName){
	this.currentRoleId=id;
	$("#membList").datagrid("clearSelections");
	$('#membList').datagrid('load',{'roleId':id});
	$('#membWin').window('setTitle',roleName+'-成员设置');
	$('#membWin').window('open');
};
//展示权限
roleList.showPermit=function(id,roleName){
	roleList.permitTreeStatus=1;//加载数据时关闭触发onCheck事件
	this.currentRoleId=id;
    $.messager.progress();
    $.post("rolemng.do?action=showPermitTree",{roleId:this.currentRoleId},function(data){
    	$('#permitTree').tree('loadData',data);
        $.messager.progress('close');
    	$('#permitWin').window('setTitle',roleName+'-权限设置');
    	$('#permitWin').window('open');
    	roleList.permitTreeStatus=0;//加载数据完成，打开触发onCheck事件
    });
};

//调整顺序
roleList.moveOrder=function(id,direct){
	$.post('rolemng.do?action=moveOrder',{'id':id,'direct':direct},function(data){
		 $('#roleList').datagrid('reload');
	});
};
//查询
roleList.query=function(){
	$("#roleList").datagrid("clearSelections");
	$('#roleList').datagrid('load',{'roleName':$("#queryForm input[name='roleName']").val(),'roleState':$("#queryForm select[name='roleState']").val()});
};
//重置
roleList.resetQuery=function(){
	$("#queryForm input[name='roleName']").val("");
	$("#queryForm select[name='roleState']").val("-1");
	roleList.query();
};


