var vehDataConvert={
	curVehModeCode:'all',
    curProtocolVersion:'all',
    editIndex:undefined,
    contextRoot:'/',
    width:800,
    height:600
};
//初始化
vehDataConvert.init=function(ctxRoot,width,height){
	this.contextRoot = ctxRoot;
    this.width = width;
    this.height = height;
	this.initTreeMenu();
	this.initVehModeTree();
	this.initConvertGrid();
    this.initDialog();
};
vehDataConvert.addVehModeVerNode=function(){
    var row=$('#vehModeTree').treegrid('getSelected');
    var dd=$('#dd');
    var ff=$('#ff');
    if(row!=null && row['vehModeCode']!='all' && row['protocolVersion']=='all'){
        dd.dialog('setTitle',row['vehModeCode']+' 添加版本');
        ff.form('load',{
            vehModeCode:row['vehModeCode'],
            protocolVersion:0,
            copyVehModeCode:''
        });
        $('#copyVehModeCode').combobox('reload');
        dd.dialog('open');
    }else{
        $.messager.alert('提示','请选择车系节点!','info');
    }
};
vehDataConvert.removeVehModeVerNode=function(){
    var row=$('#vehModeTree').treegrid('getSelected');
    if(row!=null && row['protocolVersion']!='all'){
        $.messager.confirm('确认', '清空数据转换条件吗?', function(r){
            if (r){
                $.post('/iov/metadata/vehDataConvert/removeVehProtocol',
                    {
                        'vehModeCode':row['vehModeCode'],
                        'protocolVersion':row['protocolVersion']
                    },function(data){
                        vehDataConvert.reloadVehModeTree();
                        $('#noSelectNode').attr("style","display:block");
                        $('#nodeInfo').attr("style","display:none");
                    }
                );
            }
        });
    }else{
        $.messager.alert('提示','请选择版本节点!','info');
    }
};
vehDataConvert.reloadVehModeTree=function(){
    $('#vehModeTree').treegrid('reload');
};
vehDataConvert.initDialog=function(){
    $('#dd').dialog({
        title: '版本',
        width: 400,
        height: 120,
        closed: true,
        cache: false,
        modal: true,
        buttons:[{
             text:'保存',
             iconCls: 'icon-save',
             handler:function(){
                 $.messager.progress();
                 $('#ff').form('submit', {
                     url:'/iov/metadata/vehDataConvert/addVehProtocolVersion',
                     onSubmit: function(){
                         var isValid = $(this).form('validate');
                         if (!isValid){
                             $.messager.progress('close');
                         }
                         return isValid;
                     },
                     success: function(repsData){
                         var data=eval('(' + repsData + ')');
                         if(data['success']=='false'){
                             $.messager.alert('提示',data['message'],'error');
                         }else{
                             $.messager.show({
                                 title:'提示',
                                 msg:data['message'],
                                 timeout:3000,
                                 showType:'slide'
                             });
                         }
                         $.messager.progress('close');
                         $('#dd').dialog('close');
                         vehDataConvert.reloadVehModeTree();
                     }
                 });
             }
          },{
            text:'关闭',
            iconCls: 'icon-no',
            handler:function(){
                $('#dd').dialog('close');
            }
        }]
    });
    $('#ddConver').dialog({
        title: '公式',
        width: 600,
        height: 420,
        closed: true,
        cache: false,
        modal: true,
        buttons:[{
            text:'保存',
            iconCls: 'icon-save',
            handler:function(){
                $.messager.progress();
                $('#ffConver').form('submit', {
                    url:'/iov/metadata/vehDataConvert/modifyConvert',
                    onSubmit: function(){
                        var isValid = $(this).form('validate');
                        if (!isValid){
                            $.messager.progress('close');
                        }
                        return isValid;
                    },
                    success: function(){
                        $.messager.progress('close');
                        $('#ddConver').dialog('close');
                        $('#convertList').datagrid('reload');
                    }
                });
            }
        },{
            text:'关闭',
            iconCls: 'icon-no',
            handler:function(){
                $('#ddConver').dialog('close');
            }
        }]
    });
};
//树菜单
vehDataConvert.initTreeMenu=function(){
    var treeMenu=$('#treeMenu');
    treeMenu.menu({
        minWidth:120
    });
    treeMenu.menu('appendItem',{
        text: '增加',
        iconCls: 'icon-add',
        onclick: function(){
            vehDataConvert.addVehModeVerNode();
        }
    });
    treeMenu.menu('appendItem',{
        text: '清空',
        iconCls: 'icon-remove',
        onclick: function(){
            vehDataConvert.removeVehModeVerNode();
        }
    });
    treeMenu.menu('appendItem',{
        separator: true
    });
    treeMenu.menu('appendItem',{
        text: '刷新',
        iconCls: 'icon-reload',
        onclick: function(){
            vehDataConvert.reloadVehModeTree();
        }
    });
};
//列表树
vehDataConvert.initVehModeTree=function(){
	$('#vehModeTree').treegrid({
		url:'/iov/metadata/vehDataConvert/showVehModeTree',
		loadMsg:'数据加载中...',
		iconCls:'icon-save',
		width:280,
		height:vehDataConvert.height,
		nowrap: false,
		striped: true,
		collapsible:false,
		animate:true,
		sortName: 'text',
		sortOrder: 'asc',
		remoteSort: false,
		pagination:false,
		rownumbers:true,
        idField:'id',
		treeField:'text',
        showHeader:false,
		columns:[[
            {field:'text',sortable:false,align:'left'}
		]],
		onClickRow:function(row){
            var gd=$('#convertList');
            if(row['protocolVersion']!='all'){
                vehDataConvert.curVehModeCode=row['vehModeCode'];
                vehDataConvert.curProtocolVersion=row['protocolVersion'];
                $('#noSelectNode').attr("style","display:none");
                $('#nodeInfo').attr("style","display:block");
                gd.datagrid('getPanel').panel('setTitle',vehDataConvert.curVehModeCode+' Version: '+vehDataConvert.curProtocolVersion);
                gd.datagrid('load',{'vehModeCode':vehDataConvert.curVehModeCode,'protocolVersion':vehDataConvert.curProtocolVersion});
            }else{
                $('#noSelectNode').attr("style","display:block");
                $('#nodeInfo').attr("style","display:none");
            }
		},
		onContextMenu:function(e,node){
			e.preventDefault();
			$('#vehModeTree').treegrid('select', node.id);
			$('#treeMenu').menu('show', {
				left: e.pageX,
				top: e.pageY
			});
		},
		toolbar:[{
			text:'增加',
			iconCls:'icon-add',
			handler:function(){
                vehDataConvert.addVehModeVerNode();
			}
		},{
			text:'清空',
			iconCls:'icon-remove',
			handler:function(){
                vehDataConvert.removeVehModeVerNode();			}
		},{
			text:'刷新',
			iconCls:'icon-reload',
			handler:function(){
                vehDataConvert.reloadVehModeTree();
			}
		}]
	});
};

//车系转换列表
vehDataConvert.initConvertGrid=function(){
	$('#convertList').datagrid({
		title:"车系数据转换配置",
		url:'/iov/metadata/vehDataConvert/list',
		loadMsg:'数据加载中...',
		width:vehDataConvert.width-350,
		height:vehDataConvert.height-18,
		nowrap: false,
		striped: true,
		collapsible:false,
        fitColumns:false,
        singleSelect: true,
		sortName: 'convertType',
		sortOrder: 'asc',
		remoteSort: true,
		pageSize:20,
		idField:'id',
        pagination:true,
        rownumbers:true,
        frozenColumns:[[
            {field:'id',checkbox:true}
        ]],
		columns:[[
            {field:'convertType',title:'类型',sortable:true,width:200,halign:'center',editor:{'type':'combobox','options':{
                panelWidth:'200',
                panelHeight:'150',
                valueField: 'value',
                textField: 'label',
                data:[
                    {label:'信号后置转换',value:'0'},
                    {label:'车况前置转换',value:'1'},
                    {label:'车况后置转换',value:'2'}
                ]
            }},formatter:function(value,row,index){
                if(value=='0'){
                    return '信号后置转换';
                }else if(value=='1'){
                    return '车况前置转换';
                }else if(value=='2'){
                    return '车况后置转换';
                }else{
                    return '';
                }
            }},
            {field:'dataConvert',title:'公式',sortable:false,width:100,halign:'center',formatter:function(value,row,index){
                if(value.length > 1){
                    return "<a href=\"javascript:vehDataConvert.showConvert('"+row['id']+"','"+value+"')\"><span style='color:red;'>编辑</span></a>";
                }else{
                    return "<a href=\"javascript:vehDataConvert.showConvert('"+row['id']+"','"+value+"')\"><span style='color:green;'>添加</span></a>";
                }
            }}
		]],
        toolbar:[{
            text:'增加',
            iconCls:'icon-add',
            handler:function(){
                $.post('/iov/metadata/vehDataConvert/add',
                    {
                        'vehModeCode':vehDataConvert.curVehModeCode,
                        'protocolVersion':vehDataConvert.curProtocolVersion
                    },function(data){
                        $('#convertList').datagrid('reload');
                    }
                );
            }
        },{
            text:'编辑',
            iconCls:'icon-edit',
            handler:function(){
                var gd=$("#convertList");
                var row=gd.datagrid("getSelected");
                if(row==null){
                    $.messager.alert('提示','请选择记录','info');
                    return;
                }
                var index=gd.datagrid("getRowIndex",row);
                if (vehDataConvert.editIndex != index){
                    if (vehDataConvert.endEditing()){
                        vehDataConvert.editIndex = index;
                        gd.datagrid('selectRow', index).datagrid('beginEdit',index);
                    } else {
                        setTimeout(function(){
                            gd.datagrid('selectRow', vehDataConvert.editIndex);
                        },0);
                    }
                }
            }
        },"-",{
            text:'保存',
            iconCls:'icon-save',
            handler:function(){
                var gd=$("#convertList");
                if(vehDataConvert.endEditing()){
                    gd.datagrid("acceptChanges");
                }
            }
        },"-",{
            text:'删除',
            iconCls:'icon-remove',
            handler:function(){
                var rec=$("#convertList").datagrid("getSelections");
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
                        $.post("/iov/metadata/vehDataConvert/remove",{"ids":ids},function(respData){
                            var gd=$("#convertList");
                            gd.datagrid("clearSelections");
                            gd.datagrid("reload");
                        });
                    }
                });
            }
        }],
        onDblClickRow: function(index, row){
            if (vehDataConvert.editIndex != index){
                if (vehDataConvert.endEditing()){
                    $('#convertList').datagrid('selectRow', index).datagrid('beginEdit',index);
                    vehDataConvert.editIndex = index;
                } else {
                    setTimeout(function(){
                        $('#convertList').datagrid('selectRow', vehDataConvert.editIndex);
                    },0);
                }
            }
        },
        onEndEdit: function(index,row,changes){
            var gd=$('#convertList');
            $.post('/iov/metadata/vehDataConvert/save',
                {
                 'id':row['id'],
                 'convertType':row['convertType']
                },
                function(respData) {
                    gd.datagrid('unselectAll');
                    gd.datagrid('reload');
                }
            );
        }
	});
};
//完成编辑
vehDataConvert.endEditing=function(){
    var gd=$('#convertList');
    if (vehDataConvert.editIndex == undefined){
        return true
    }
    if (gd.datagrid('validateRow', vehDataConvert.editIndex)){
        gd.datagrid('endEdit', vehDataConvert.editIndex);
        vehDataConvert.editIndex = undefined;
        return true;
    }
    return false;
};
//编辑公式
vehDataConvert.showConvert=function(dataConvertId,value){
    var dd=$('#ddConver');
    var ff=$('#ffConver');
    ff.form('load',{
        id:dataConvertId,
        dataConvert:$.base64.decode(value)
    });
    dd.dialog('open');
};