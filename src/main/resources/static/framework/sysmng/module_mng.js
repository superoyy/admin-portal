var moduleMng={
    contextRoot:"/",
    treeHeight:490
};
//初始化
moduleMng.init=function(ctxRoot,treeHeight){
    this.contextRoot = ctxRoot;
    this.treeHeight = treeHeight;
	this.initModuleTreeMenu();
	this.initModuleTree();
    this.initModuleFuncListWin();
    this.initModuleFuncList();
	this.initModuleFuncWin();
    this.initIcon();
};
moduleMng.initIcon = function(){
    $("#iconTable p").css({"border-color":"red","border-style":"none","border-width":"thin","cursor":"hand"});
    $("#iconTable p").on("click",function(){
        var iconClassName= $.trim($(this).first("li").text());
        $("#iconTable p").css({"border-style":"none"});
        $("#selectedIcon").removeClass();
        $(this).css({"border-style":"solid"});
        $("#selectedIcon").addClass(iconClassName);
        $("#ff input[name='icon']").val(iconClassName);
    });
};
//模块树菜单
moduleMng.initModuleTreeMenu=function(){
	$('#moduleTreeMenu').menu({
		minWidth:120,
		onClick:function(item){
			var node=$('#moduleTree').treegrid('getSelected');
			if(item.name=="add"){
				moduleMng.addModule(node);
			}else if(item.name=="remove"){
				moduleMng.removeModule(node);
			}else if(item.name=="reload"){
				moduleMng.reload();
			}else if(item.name=="collapse"){
				moduleMng.collapse(node.id);
			}else if(item.name=="expand"){
				moduleMng.expand(node.id);
			}else if(item.name=="top"){
				moduleMng.moveSysModuleOrder(node.id,"top");
			}else if(item.name=="up"){
				moduleMng.moveSysModuleOrder(node.id,"up");
			}else if(item.name=="down"){
				moduleMng.moveSysModuleOrder(node.id,"down");
			}else if(item.name=="bottom"){
				moduleMng.moveSysModuleOrder(node.id,"bottom");
			}
		}
	});
};
//模块列表树
moduleMng.initModuleTree=function(){
	$('#moduleTree').treegrid({
		url:'modulemng.do?action=list',
		loadMsg:'数据加载中...',
		iconCls:'icon-save',
		width:300,
		height:moduleMng.treeHeight,
		nowrap: false,
		striped: true,
		collapsible:false,
		animate:true,
		sortName: 'moduleOrder',
		sortOrder: 'asc',
		remoteSort: true,
		idField:'id',
		pagination:false,
		rownumbers:true,
		treeField:'moduleName',
		columns:[[
            {field:'moduleName',sortable:false,width:261,align:'left'}
		]],
		onClickRow:function(row){
			moduleMng.showModuleInfo(row.id);
		},
		onContextMenu:function(e,node){
			e.preventDefault();
			$('#moduleTree').treegrid('select', node.id);
			$('#moduleTreeMenu').menu('show', {
				left: e.pageX,
				top: e.pageY
			});
		},
		toolbar:[{
			text:'增加',
			iconCls:'icon-plus',
			handler:function(){
				var node=$('#moduleTree').treegrid('getSelected');
				moduleMng.addModule(node);				
			}
		},{
			text:'删除',
			iconCls:'icon-remove',
			handler:function(){
				var node=$('#moduleTree').treegrid('getSelected');
				moduleMng.removeModule(node);
			}
		},"-",{
			text:'展开',
			iconCls:'icon-expand',
			handler:function(){
				moduleMng.expand("0");
			}
		},{
			text:'收起',
			iconCls:'icon-collapse',
			handler:function(){
				moduleMng.collapse("0");
			}
		},{
			text:'刷新',
			iconCls:'icon-refresh',
			handler:function(){
				moduleMng.reload();
			}
		}]
	});
};
//模块功能列表
moduleMng.initModuleFuncList=function(){
	$('#moduleFuncList').datagrid({
		url:'modulemng.do?action=listModuleFunc',
		loadMsg:'数据加载中...',
		height:340,
		nowrap: false,
		striped: true,
		collapsible:false,
		sortName: 'funcOrder',
		sortOrder: 'desc',
		remoteSort: true,
		pageSize:10,
		idField:'id',
		frozenColumns:[[
   		    {field:'id',checkbox:true}
   		]],
		columns:[[
            {field:'funcName',title:'名称',sortable:false,width:120,halign:'center'},
            {field:'url',title:'路径',sortable:false,width:300,halign:'center'},
            {field:'permitRols',title:'可用角色',sortable:false,width:100,halign:'center',formatter:function(value,row,index){
            	var roleNames="";
            	for(var i=0;i<value.length;i++){
            		roleNames+=value[i].roleName+"<br/>";
            	}
            	return roleNames;
            }},
            {field:'funcDesc',title:'说明',sortable:false,width:200,halign:'center'}
		]],
		pagination:true,
		rownumbers:true,
		onDblClickRow:function(rowIndex, row){
			moduleMng.showModuleFunc(row.id);
		},
		toolbar:[{
			text:'增加',
			iconCls:'icon-plus',
			handler:function(){
				moduleMng.showModuleFunc();
			}
		},{
			text:'编辑',
			iconCls:'icon-edit',
			handler:function(){
				var rec=$("#moduleFuncList").datagrid("getSelections");
				if(rec.length==0){
					$.messager.alert('提示','请选择记录','info');
				}else{
					moduleMng.showModuleFunc(rec[0].id);
				}
			}
		},{
			text:'删除',
			iconCls:'icon-remove',
			handler:function(){
				var rec=$("#moduleFuncList").datagrid("getSelections");
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
                        $.post("modulemng.do?action=removeModuleFunc",{"ids":ids},function(data){
                            $("#moduleFuncList").datagrid("clearSelections");
                            $("#moduleFuncList").datagrid("reload");
                        });
                    }
                });
			}
		},'-',{
			iconCls: 'icon-upload-alt',
			text:"置顶",
			handler: function(){
				var rec=$('#moduleFuncList').datagrid('getSelected');
				if(rec==null){
					$.messager.alert('提示','请选择记录','info');
				}else{
					moduleMng.moveModuleFuncOrder(rec.id,'top');				
				}
			}
		},{
			iconCls: 'icon-sort-up',
			text:"上移",
			handler: function(){
				var rec=$('#moduleFuncList').datagrid('getSelected');
				if(rec==null){
					$.messager.alert('提示','请选择记录','info');
				}else{
					moduleMng.moveModuleFuncOrder(rec.id,'up');				
				}
			}
		},{
			iconCls: 'icon-sort-down',
			text:"下移",
			handler: function(){
				var rec=$('#moduleFuncList').datagrid('getSelected');
				if(rec==null){
					$.messager.alert('提示','请选择记录','info');
				}else{
					moduleMng.moveModuleFuncOrder(rec.id,'down');				
				}
			}
		},{
			iconCls: 'icon-download-alt',
			text:"置底",
			handler: function(){
				var rec=$('#moduleFuncList').datagrid('getSelected');
				if(rec==null){
					$.messager.alert('提示','请选择记录','info');
				}else{
					moduleMng.moveModuleFuncOrder(rec.id,'bottom');				
				}
			}
		}]
	});
};
moduleMng.initModuleFuncListWin=function(){
    $('#moduleFuncListWin').window({
        width:800,
        height:380,
        top:20,
        modal:true,
        title:'功能点-清单',
        iconCls:'icon-edit',
        collapsible:false,
        minimizable:false,
        maximizable:false,
        resizable:false,
        closable:true,
        closed:true,
        onMove:function(left,top){
            if(top < 0){
                $('#moduleFuncListWin').window('move',{'left':left,'top':0});
            }
        }
    });
};

//功能项编辑窗口
moduleMng.initModuleFuncWin=function(){
    $('#moduleFuncWin').window({
        width:800,
        height:380,
        top:20,
        modal:true,
        title:'功能点-编辑',
        iconCls:'icon-edit',
        collapsible:false,
        minimizable:false,
        maximizable:false,
        resizable:false,
        closable:true,
        closed:true,
        onMove:function(left,top){
            if(top < 0){
                $('#moduleFuncWin').window('move',{'left':left,'top':0});
            }
        }
    });
};
//保存模块
moduleMng.save=function(){
	$('#ff').form('submit',{
		  url:'modulemng.do?action=save',  
		  onSubmit: function(){
			 if($('#ff').form('validate')){
				 $.messager.progress();
				 return true;
			 }
			 return false;
		  },  
		  success:function(responseText){
			 var data = eval('(' + responseText + ')'); 
		     $('#moduleTree').treegrid('update',{'id':data.id,'row':{'moduleName':data.moduleName}});
		     $('#moduleTree').treegrid('select',data.id);
		     $('#moduleFuncList').datagrid('load');
			 $.messager.progress('close');
			 $.messager.show({
			 	title:'提示',
			    msg:'保存成功保存:['+data.moduleName+"]节点数据",
				timeout:8000,
				showType:'slide'
			 });	 
		  }  
	});
};
//保存模块功能项
moduleMng.saveModuleFunc=function(){
	$('#moduleFuncForm').form('submit',{
		  url:'modulemng.do?action=saveModuleFunc',  
		  onSubmit: function(){
			 if($('#moduleFuncForm').form('validate')){
				 $.messager.progress();
				 return true;
			 }
			 return false;
		  },  
		  success:function(responseText){
			 var data = eval('(' + responseText + ')'); 
			 $.messager.progress('close');
			 $.messager.show({
			 	title:'提示',
			    msg:'保存成功保存:['+data.funcName+"]数据",
				timeout:8000,
				showType:'slide'
			 });
		     $('#moduleFuncWin').window('close');
		 	 $('#moduleFuncList').datagrid('reload');
		  }  
	});
};
//增加模块
moduleMng.addModule=function(parentNode){
    if(!parentNode){
       $.messager.alert('系统提示','请选中节点!','info');
       return;
    }
    $.post("modulemng.do?action=save",{upId:parentNode.id},function(responseText){
		var data = eval('(' + responseText + ')');    
        $('#moduleTree').treegrid('append',{parent:parentNode.id,data:[{id:data.id,moduleName:data.moduleName}]});
    	$('#moduleTree').treegrid('refresh',parentNode.id);    	
    	$('#moduleTree').treegrid('select',data.id);
    	moduleMng.showModuleInfo(data.id);
    });
};
//得到模块路径
moduleMng.getModulePath=function(moduleId,path){
	var pNode=$('#moduleTree').treegrid('getParent',moduleId);
	var p=path;
	if(pNode){
		p=pNode.moduleName+"/"+p;
		p=this.getModulePath(pNode.id,p);
	}
	return p;
};
//展示模块信息
moduleMng.showModuleInfo=function(moduleId){
	var path;
	if(moduleId=="0"){
		$('#noSelectNode').attr("style","display:block");
		$('#nodeInfo').attr("style","display:none");
	}else{
		path=this.getModulePath(moduleId,"");
		$('#modulePath').text(path);
		$.post("modulemng.do?action=show",{"id":moduleId},function(data){
			$('#noSelectNode').attr("style","display:none");
			$('#nodeInfo').attr("style","display:block");
			$('#moduleFuncList').datagrid('resize');
			$('#ff').form('load',data);
			$("input[name='permitRole']").each(function(){
				this.checked=false;
				for(var i=0;i<data.permit.length;i++){
					if(data.permit[i].roleId==this.value){
						this.checked=true;
						break;
					}
				}
			});
            $("#iconTable p").css({"border-style":"none"});
            $("#selectedIcon").removeClass();
            if(data['icon'] != null && data['icon'].length > 0){
                $("#iconTable p").each(function(){
                    var iconClassName = $.trim($(this).first("li").text());
                    if(data['icon'] == iconClassName){
                        $(this).css({"border-style":"solid"});
                        $("#selectedIcon").addClass(iconClassName);
                        return;
                    }
                });
            }
			$('#moduleFuncList').datagrid('load',{"moduleId":moduleId});
		});
	}
};
//展示模块功能项信息
moduleMng.showModuleFunc=function(funcId){
	var moduleId=$("#ff input[name='id']").val();
	$('#moduleFuncForm').form('clear');
	if(funcId){
		$.post("modulemng.do?action=showModuleFunc",{id:funcId},function(data){
			$('#moduleFuncForm').form('load',data);
			$("input[name='permitFuncRole']").each(function(){
				this.checked=false;
				for(var i=0;i<data.permit.length;i++){
					if(data.permit[i].roleId==this.value){
						this.checked=true;
						break;
					}
				}
			});
		});
	}else{
		$("input[name='permitFuncRole']").each(function(){
			this.checked=false;
		});
	}
	$("#moduleFuncForm input[name='moduleId']").val(moduleId);
	$('#moduleFuncWin').window('open');
};
moduleMng.showModuleFuncListWin = function(){
    $('#moduleFuncListWin').window('open');
};
//删除模块
moduleMng.removeModule=function(node){
  if(node!=null){
    if(node.id=="0"){
      $.messager.alert('系统提示','不能删除根节点!','info');
      return;
    }
    $.messager.confirm("系统提示", "确定删除节点:<span style='font:bold;'>"+node.moduleName+"</span>及其下级节点吗?", function(r){
		if (r){
			$.messager.progress();
			$.post("modulemng.do?action=remove",{id:node.id},function(data){
			    var parentNode=$('#moduleTree').treegrid('getParent',node.id);
				$('#moduleTree').treegrid('remove',node.id);
			    $('#moduleTree').treegrid('select',parentNode.id);
			    moduleMng.showModuleInfo(parentNode.id);			    
				$.messager.progress('close');
			});
		}
	});
  }else{
    $.messager.alert('系统提示','请选中节点!','info');
  }
};
//收起
moduleMng.collapse = function(id){
  $('#moduleTree').treegrid('collapseAll',id);
};
//展开
moduleMng.expand = function(id){
  $('#moduleTree').treegrid('expandAll',id);
};
//刷新
moduleMng.reload = function(){
	$('#moduleTree').treegrid('reload');
};
//调整模块顺序
moduleMng.moveSysModuleOrder=function(id,direct){
    $.post('modulemng.do?action=moveOrder',{'id':id,'direct':direct},function(data){
        $('#moduleTree').treegrid('reload');
    });
};
//调整功能项顺序
moduleMng.moveModuleFuncOrder=function(id,direct){
    $.post('modulemng.do?action=moveModuleFuncOrder',{'id':id,'direct':direct},function(data){
        $("#moduleFuncList").datagrid('reload');
    });
};

moduleMng.changeFuncPermit=function(checked,value){
    if(checked){
        $("#ff").find("input[name='permitRole']").each(function(){
            if(this.value == value){
                this.checked=true;
            }
        });
    }
};
