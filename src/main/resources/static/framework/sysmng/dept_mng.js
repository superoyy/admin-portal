var deptMng={
	currentDeptId:"",
    contextRoot:"/",
    width:800,
    height:600
};
//初始化
deptMng.init=function(ctxRoot,width,height){
	this.contextRoot = ctxRoot;
    this.width = width;
    this.height = height;
	this.initDeptTreeMenu();
	this.initDeptTree();
	this.initDeptMembList();
	this.initForm();	
};
//树菜单
deptMng.initDeptTreeMenu=function(){
	$('#deptTreeMenu').menu({
		minWidth:120,
		onClick:function(item){
			var node=$('#deptTree').treegrid('getSelected');
			if(item.name=="add"){
				deptMng.add(node);
			}else if(item.name=="remove"){
				deptMng.remove(node);
			}else if(item.name=="reload"){
				deptMng.reload();
			}else if(item.name=="collapse"){
				deptMng.collapse(node.id);
			}else if(item.name=="expand"){
				deptMng.expand(node.id);
			}else if(item.name=="top"){
				deptMng.moveOrder(node.id,"top");
			}else if(item.name=="up"){
				deptMng.moveOrder(node.id,"up");
			}else if(item.name=="down"){
				deptMng.moveOrder(node.id,"down");
			}else if(item.name=="bottom"){
				deptMng.moveOrder(node.id,"bottom");
			}
		}
	});
};
//列表树
deptMng.initDeptTree=function(){
	$('#deptTree').treegrid({
		url:'deptmng.do?action=list',
		loadMsg:'数据加载中...',
		iconCls:'icon-save',
		width:290,
		height:deptMng.height,
		nowrap: false,
		striped: true,
		collapsible:false,
		animate:true,
		sortName: 'deptOrder',
		sortOrder: 'asc',
		remoteSort: true,
		idField:'id',
		pagination:false,
		rownumbers:true,
		treeField:'deptName',
		columns:[[
            {field:'deptName',sortable:false,width:261,align:'left',formatter:function(value,row,index){
            	if(row.deptState=="1"){
            		return "<span style='color:red;'>"+value+"</span>";
            	}else{
            		return value;
            	}
            }}
		]],
		onClickRow:function(row){
			deptMng.show(row.id);
		},
		onContextMenu:function(e,node){
			e.preventDefault();
			$('#deptTree').treegrid('select', node.id);
			$('#deptTreeMenu').menu('show', {
				left: e.pageX,
				top: e.pageY
			});
		},
		toolbar:[{
			text:'增加',
			iconCls:'icon-add',
			handler:function(){
				var node=$('#deptTree').treegrid('getSelected');
				deptMng.add(node);				
			}
		},{
			text:'删除',
			iconCls:'icon-remove',
			handler:function(){
				var node=$('#deptTree').treegrid('getSelected');
				deptMng.remove(node);
			}
		},"-",{
			text:'展开',
			iconCls:'icon-expand',
			handler:function(){
				deptMng.expand("0");
			}
		},{
			text:'收起',
			iconCls:'icon-collapse',
			handler:function(){
				deptMng.collapse("0");
			}
		},{
			text:'刷新',
			iconCls:'icon-reload',
			handler:function(){
				deptMng.reload();
			}
		}]
	});
};
//部门成员列表
deptMng.initDeptMembList=function(){
	$('#deptMembList').datagrid({
		title:"成员",
		url:'deptmng.do?action=listMemb',
		loadMsg:'数据加载中...',
		width:deptMng.width-330,
		height:deptMng.height-240,
		nowrap: false,
		striped: true,
		collapsible:false,
        fitColumns:true,
		sortName: 'loginOrder',
		sortOrder: 'asc',
		remoteSort: true,
		pageSize:20,
		idField:'id',
		columns:[[
            {field:'loginName',title:'登录名',sortable:true,width:120,halign:'center'},
            {field:'userName',title:'用户名',sortable:true,width:120,halign:'center'},
            {field:'mailAddr',title:'邮箱',sortable:true,width:120,halign:'center'},
            {field:'phoneNum',title:'电话',sortable:true,width:120,halign:'center'},
            {field:'loginState',title:'状态',sortable:true,width:80,halign:'center',formatter:function(value,row,index){
            	if(value=="0"){
            		return "<span style='color:green;'>启用</span>";
            	}else if(value=="1"){
            		return "<span style='color:red;'>禁用</span>";
            	}
            }},
		    {field:'isMemb',title:'是/否成员',sortable:true,width:100,halign:'center',formatter:function(value,row,index){
		    	if(value){
			    	return "<input id='memb_"+row.id+"' value='"+row.id+"' type='checkbox' checked onclick='deptMng.updateMemb(this)' />";
		    	}else{
			    	return "<input id='memb_"+row.id+"' value='"+row.id+"' type='checkbox' onclick='deptMng.updateMemb(this)' />";
		    	}
            }}
		]],
		pagination:true,
		rownumbers:true
	});
};
//表单
deptMng.initForm=function(){
	$("#ff input[name='deptName']").validatebox({
		   required: true,  
		   validType: 'length[1,30]',
		   missingMessage:'必填',
		   invalidMessage:'30字内'	   
	});
	$("#ff input[name='deptNo']").validatebox({  
		   required: true,  
		   validType: 'length[1,100]',
		   missingMessage:'必填',
		   invalidMessage:'100字内'	   
	}); 
	$("#ff textarea[name='deptDesc']").validatebox({  
		   validType: 'length[0,300]',
		   invalidMessage:'300字内'	   
	}); 
};
//保存
deptMng.save=function(){
	$('#ff').form('submit',{
		  url:'deptmng.do?action=save',  
		  onSubmit: function(){
			 var ok="false";
			 if($('#ff').form('validate')){
				 ok = $.ajax({
					  type: "POST",
					  url: "deptmng.do?action=isUqDeptNo",
					  data:{"id":$("#ff input[name='id']").val(),"deptNo":$("#ff input[name='deptNo']").val()},
					  async: false
				 }).responseText; 				 
			 }
			 if(ok=="true"){
				 $.messager.progress();
				 return true;
			 }else{
				 $.messager.alert('系统提示','部门代码已经存在!','info');
				 return false;
			 }
		  },  
		  success:function(responseText){
			 var data = eval('(' + responseText + ')');
			 var deptName=data.deptName;
			 if(data.deptState=="1"){
				 deptName="<span style='color:red;'>"+data.deptName+"</span>";
			 }
		     $('#deptTree').treegrid('update',{id:data.id,row:{'deptName':deptName,'deptState':data.deptState}});
		     $('#deptTree').treegrid('select',data.id);
			 $.messager.progress('close');
			 $.messager.show({
			 	title:'提示',
			    msg:'保存成功:['+data.deptName+"]数据",
				timeout:5000,
				showType:'slide'
			 });	 
		  }  
	});
};
//增加
deptMng.add=function(parentNode){
    if(!parentNode){
       $.messager.alert('系统提示','请选中节点!','info');
       return;
    }
    $.post("deptmng.do?action=save",{upId:parentNode.id},function(responseText){
		var data = eval('(' + responseText + ')');    
        $('#deptTree').treegrid('append',{parent:parentNode.id,data:[{id:data.id,deptName:data.deptName}]});
    	$('#deptTree').treegrid('refresh',parentNode.id);    	
    	$('#deptTree').treegrid('select',data.id);
    	deptMng.show(data.id);
    });
};
//得到路径
deptMng.getPath=function(deptId,path){
	var pNode=$('#deptTree').treegrid('getParent',deptId);
	var p=path;
	if(pNode){
		p=pNode.deptName+"/"+p;
		p=this.getPath(pNode.id,p);
	}
	return p;
};
//展示
deptMng.show=function(deptId){
	this.currentDeptId=deptId;
	var path;
	if(deptId=="0"){
		$('#noSelectNode').attr("style","display:block");
		$('#nodeInfo').attr("style","display:none");
	}else{
		path=this.getPath(deptId,"");
		$('#path').html(path);
		$.post("deptmng.do?action=show",{id:deptId},function(data){
			$('#noSelectNode').attr("style","display:none");
			$('#nodeInfo').attr("style","display:block");
			$('#deptMembList').datagrid('resize');
			$('#ff').form('load',data);
			$('#deptMembList').datagrid('load',{"deptId":deptMng.currentDeptId});
		});
	}
};
//删除
deptMng.remove=function(node){
  if(node!=null){
    if(node.id=="0"){
      $.messager.alert('系统提示','不能删除根节点!','info');
      return;
    }
    $.messager.confirm("系统提示", "确定删除节点:<span style='font:bold;'>"+node.deptName+"</span>及其下级节点吗?", function(r){
		if (r){
			$.messager.progress();
			$.post("deptmng.do?action=remove",{id:node.id},function(data){
			    var parentNode=$('#deptTree').treegrid('getParent',node.id);
				$('#deptTree').treegrid('remove',node.id);
			    $('#deptTree').treegrid('select',parentNode.id);
			    deptMng.show(parentNode.id);			    
				$.messager.progress('close');
			});
		}
	});
  }else{
    $.messager.alert('系统提示','请选中节点!','info');
  }
};
//收起
deptMng.collapse=function(id){
  $('#deptTree').treegrid('collapseAll',id);
};
//展开
deptMng.expand=function(id){
  $('#deptTree').treegrid('expandAll',id);
};
//刷新
deptMng.reload=function(){
	$('#deptTree').treegrid('reload');
};
//调整顺序
deptMng.moveOrder=function(id,direct){
	$.post('deptmng.do?action=moveOrder',{'id':id,'direct':direct},function(data){
		$('#deptTree').treegrid('reload');
	});
};
//选择成员
deptMng.updateMemb=function(checkbox){
	$.post("deptmng.do?action=updateMemb",{deptId:this.currentDeptId,loginId:checkbox.value,isMemb:checkbox.checked},function(data){
		 $.messager.show({
		 	title:'提示',
		    msg:'成功更新成员',
			timeout:1000,
			showType:'slide'
	     });
	});
};


