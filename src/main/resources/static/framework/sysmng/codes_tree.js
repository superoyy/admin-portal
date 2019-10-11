var codesTree={
	codeType:"",
    contextRoot:"/",
    treeWidth:290,
    treeHeight:490
};
//初始化
codesTree.init=function(ctxRoot,cType,treeWidth,treeHeight){
    this.treeWidth=treeWidth;
    this.treeHeight=treeHeight;
	this.contextRoot=ctxRoot;
	this.codeType=cType;
	this.initCodesTreeMenu();
	this.initCodesTree();	
	this.initForm();	
};
//树菜单
codesTree.initCodesTreeMenu=function(){
	$('#codesTreeMenu').menu({
		minWidth:120,
		onClick:function(item){
			var node=$('#codesTree').treegrid('getSelected');
			if(item.name=="add"){
				codesTree.add(node);
			}else if(item.name=="remove"){
				codesTree.remove(node);
			}else if(item.name=="reload"){
				codesTree.reload();
			}else if(item.name=="collapse"){
				codesTree.collapse(node.id);
			}else if(item.name=="expand"){
				codesTree.expand(node.id);
			}else if(item.name=="top"){
				codesTree.moveOrder(node.id,"bottom");
			}else if(item.name=="up"){
				codesTree.moveOrder(node.id,"down");
			}else if(item.name=="down"){
				codesTree.moveOrder(node.id,"up");
			}else if(item.name=="bottom"){
				codesTree.moveOrder(node.id,"top");
			}
		}
	});
};
//树
codesTree.initCodesTree=function(){
	$('#codesTree').treegrid({
		url:'codesmng.do?action=treeList&codeType='+codesTree.codeType,
		loadMsg:'数据加载中...',
		iconCls:'icon-save',
		width:codesTree.treeWidth,
		height:codesTree.treeHeight,
		nowrap: false,
		striped: true,
		collapsible:false,
		animate:true,
		sortName: 'codeOrder',
		sortOrder: 'asc',
		remoteSort: true,
		idField:'id',
		pagination:false,
		rownumbers:true,
		treeField:'codeName',
		columns:[[
            {field:'codeName',sortable:false,width:261,align:'left'}
		]],
		onClickRow:function(row){
			codesTree.show(row.id);
		},
		onContextMenu:function(e,node){
			e.preventDefault();
			$('#codesTree').treegrid('select', node.id);
			$('#codesTreeMenu').menu('show', {
				left: e.pageX,
				top: e.pageY
			});
		},
		toolbar:[{
			text:'增加',
			iconCls:'icon-add',
			handler:function(){
				var node=$('#codesTree').treegrid('getSelected');
				codesTree.add(node);				
			}
		},{
			text:'删除',
			iconCls:'icon-remove',
			handler:function(){
				var node=$('#codesTree').treegrid('getSelected');
				codesTree.remove(node);
			}
		},"-",{
			text:'展开',
			iconCls:'icon-expand',
			handler:function(){
				codesTree.expand("0");
			}
		},{
			text:'收起',
			iconCls:'icon-collapse',
			handler:function(){
				codesTree.collapse("0");
			}
		},{
			text:'刷新',
			iconCls:'icon-reload',
			handler:function(){
				codesTree.reload();
			}
		}]
	});
};
//表单
codesTree.initForm=function(){
	//隔行变色
	//$("#ff > p:even").css({backgroundColor:'#f3ffe3'});
	//验证
	$("#ff").find("input[name='codeName']").validatebox({
	   required: true,  
	   validType: 'length[1,30]',
	   missingMessage:'必填',
	   invalidMessage:'30字内'	   
	});
	$("#ff").find("input[name='codeKey']").validatebox({
	   required: true,  
	   validType: 'length[1,100]',
	   missingMessage:'必填',
	   invalidMessage:'100字内'	   
	}); 
	$("#ff").find("input[name='codeValue']").validatebox({
	   validType: 'length[0,200]',
	   invalidMessage:'200字内'	   
	}); 
	$("#ff").find("textarea[name='codeDesc']").validatebox({
	   validType: 'length[0,300]',
	   invalidMessage:'300字内'	   
	}); 
	$("#ff").find("input[name='remainField']").validatebox({
	   validType: 'length[0,30]',
	   invalidMessage:'30字内'	   
	}); 
};

//保存
codesTree.save=function(){
	$('#ff').form('submit',{
		  url:'codesmng.do?action=save&codeType='+codesTree.codeType,  
		  onSubmit: function(){
			 var ok="false";
			 if($('#ff').form('validate')){
				 ok = $.ajax({
					  type: "POST",
					  url: "codesmng.do?action=isUqCodeKey",
					  data:{"id":$("#ff input[name='id']").val(),"codeType":codesTree.codeType,"codeKey":$("#ff input[name='codeKey']").val()},
					  async: false
				 }).responseText; 				 
			 }
			 if(ok=="true"){
				 $.messager.progress();
				 return true;
			 }else{
				 $.messager.alert('系统提示','代码已经存在!','info');
				 return false;
			 }
		  },  
		  success:function(responseText){
			 $.messager.progress('close');
			 $('#codesTree').treegrid('reload');
			 $.messager.show({
				 	title:'提示',
				    msg:'保存成功!',
					timeout:8000,
					showType:'slide'
		     });
		  }  
	});
};
//增加
codesTree.add=function(parentNode){
    if(!parentNode){
       $.messager.alert('系统提示','请选中节点!','info');
       return;
    }
    $.post("codesmng.do?action=add",{codeType:codesTree.codeType,upId:parentNode.id},function(data){
        $('#codesTree').treegrid('append',{parent:parentNode.id,data:[{id:data.id,codeName:data.codeName}]});
    	$('#codesTree').treegrid('refresh',parentNode.id);    	
    	$('#codesTree').treegrid('select',data.id);
    	codesTree.show(data.id);
    });
};
//得到路径
codesTree.getPath=function(id,path){
	var pNode=$('#codesTree').treegrid('getParent',id);
	var p=path;
	if(pNode){
		p=pNode.codeName+"/"+p;
		p=this.getPath(pNode.id,p);
	}
	return p;
};
//展示
codesTree.show=function(id){
	var path;
	if(id=="0"){
		$('#noSelectNode').attr("style","display:block");
		$('#nodeInfo').attr("style","display:none");
	}else{
		path=this.getPath(id,"");
		$('#path').text(path);
		$.post("codesmng.do?action=show",{"id":id},function(data){
			$('#noSelectNode').attr("style","display:none");
			$('#nodeInfo').attr("style","display:block");
			$('#ff').form('load',data);
		});
	}
};
//删除
codesTree.remove=function(node){
  if(node!=null){
    if(node.id=="0"){
      $.messager.alert('系统提示','不能删除根节点!','info');
      return;
    }
    $.messager.confirm("系统提示", "确定删除节点:<span style='font:bold;'>"+node.codeName+"</span>及其下级节点吗?", function(r){
		if (r){
			$.messager.progress();
			$.post("codesmng.do?action=remove",{ids:node.id},function(data){
			    var parentNode=$('#codesTree').treegrid('getParent',node.id);
				$('#codesTree').treegrid('remove',node.id);
			    $('#codesTree').treegrid('select',parentNode.id);
			    codesTree.show(parentNode.id);			    
				$.messager.progress('close');
			});
		}
	});
  }else{
    $.messager.alert('系统提示','请选中节点!','info');
  }
};
//收起
codesTree.collapse=function(id){
  $('#codesTree').treegrid('collapseAll',id);
};
//展开
codesTree.expand=function(id){
  $('#codesTree').treegrid('expandAll',id);
};
//刷新
codesTree.reload=function(id){
	$('#codesTree').treegrid('reload');
};
//调整节点顺序
codesTree.moveOrder=function(id,direct){
	$.post('codesmng.do?action=moveOrder',{'id':id,'direct':direct},function(data){
		$('#codesTree').treegrid('reload');
	});
};
