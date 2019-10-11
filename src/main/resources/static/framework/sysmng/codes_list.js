var codesList={
    contextRoot:"/",
    codeType:"",
    width:600,
    height:400
};
//初始化
codesList.init=function(ctxRoot,cType,gridWidth,gridHeight){
	this.contextRoot=ctxRoot;
	this.codeType=cType;
	this.width=gridWidth;
    this.height=gridHeight;
	this.initCodesList();
	this.initDetailWin();
	this.initForm();	
};
//列表
codesList.initCodesList=function(){
	$('#codesList').datagrid({
		url:'codesmng.do?action=list&codeType='+codesList.codeType,
		loadMsg:'数据加载中...',
		width:codesList.width,
		height:codesList.height,
		nowrap: false,
		striped: true,
		collapsible:false,
        fitColumns:true,
		sortName: 'codeOrder',
		sortOrder: 'desc',
		remoteSort: true,
		pageSize:20,
		idField:'id',
		frozenColumns:[[
   		    {field:'id',checkbox:true}
   		]],
		columns:[[
            {field:'codeName',title:'名称',sortable:true,width:120,halign:'center'},
            {field:'codeKey',title:'代码',sortable:true,width:120,halign:'center'},
            {field:'codeValue',title:'码值',sortable:true,width:300,halign:'center'},
            {field:'codeState',title:'状态',sortable:true,width:60,align:'center',formatter:function(value,row,index){
            	if(value=="0"){
            		return "<span style='color:green;'>有效</span>";
            	}else if(value=="1"){
            		return "<span style='color:red;'>无效</span>";
            	}
            }},
            {field:'codeDesc',title:'说明',sortable:true,width:120,halign:'center'},
            {field:'remainField',title:'预留',sortable:true,width:100,halign:'center'}
		]],
		pagination:true,
		rownumbers:true,
		onDblClickRow:function(rowIndex, row){
			codesList.showDetail(row.id);
		},
		toolbar:[{
			text:'增加',
			iconCls:'icon-add',
			handler:function(){
				codesList.showDetail();
			}
		},{
			text:'编辑',
			iconCls:'icon-edit',
			handler:function(){
				var rec=$("#codesList").datagrid("getSelections");
				if(rec.length==0){
					$.messager.alert('提示','请选择记录','info');
				}else{
					codesList.showDetail(rec[0].id);
				}
			}
		},{
			text:'删除',
			iconCls:'icon-remove',
			handler:function(){
				var rec=$("#codesList").datagrid("getSelections");
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
                        $.post("codesmng.do?action=remove",{"ids":ids},function(data){
                            var gd=$("#codesList");
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
				var rec=$('#codesList').datagrid('getSelected');
				if(rec==null){
					$.messager.alert('提示','请选择记录','info');
				}else{
					codesList.moveOrder(rec.id,'top');				
				}
			}
		},{
			iconCls: 'icon-up',
			text:"上移",
			handler: function(){
				var rec=$('#codesList').datagrid('getSelected');
				if(rec==null){
					$.messager.alert('提示','请选择记录','info');
				}else{
					codesList.moveOrder(rec.id,'up');				
				}
			}
		},{
			iconCls: 'icon-down',
			text:"下移",
			handler: function(){
				var rec=$('#codesList').datagrid('getSelected');
				if(rec==null){
					$.messager.alert('提示','请选择记录','info');
				}else{
					codesList.moveOrder(rec.id,'down');				
				}
			}
		},{
			iconCls: 'icon-bottom',
			text:"置底",
			handler: function(){
				var rec=$('#codesList').datagrid('getSelected');
				if(rec==null){
					$.messager.alert('提示','请选择记录','info');
				}else{
					codesList.moveOrder(rec.id,'bottom');				
				}
			}
		}]
	});
};
//表单窗口
codesList.initDetailWin=function(){
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
codesList.initForm=function(){
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
codesList.save=function(){
	$('#ff').form('submit',{
		  url:'codesmng.do?action=save&codeType='+codesList.codeType,  
		  onSubmit: function(){
			 var ok="false";
			 if($('#ff').form('validate')){
				 ok = $.ajax({
					  type: "POST",
					  url: "codesmng.do?action=isUqCodeKey",
					  data:{"id":$("#ff input[name='id']").val(),"codeType":codesList.codeType,"codeKey":$("#ff input[name='codeKey']").val()},
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
			 $('#detailWin').window('close');			 
			 $('#codesList').datagrid('reload');
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
codesList.showDetail=function(id){
	$('#ff').form('clear');
	if(id){
		$.post("codesmng.do?action=show",{"id":id},function(data){
			$('#ff').form('load',data);
		});
	}else{
		$("#ff select[name='codeState']").val('0');
	}
	$('#detailWin').window('open');
};
//调整顺序
codesList.moveOrder=function(id,direct){
	$.post('codesmng.do?action=moveOrder',{'id':id,'direct':direct},function(data){
		 $('#codesList').datagrid('reload');
	});
};
//查询
codesList.query=function(){
	$("#codesList").datagrid("clearSelections");
	$('#codesList').datagrid('load',{'codeName':$("#queryForm input[name='codeName']").val(),'codeKey':$("#queryForm input[name='codeKey']").val(),'codeValue':$("#queryForm input[name='codeValue']").val()});
};
//重置
codesList.resetQuery=function(){
	$("#queryForm input[name='codeName']").val("");
	$("#queryForm input[name='codeKey']").val("");
	$("#queryForm input[name='codeValue']").val("");
	codesList.query();
};


