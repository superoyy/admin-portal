var vehDidSignal={
	curVehModeCode:'all',
    curProtocolVersion:'all',
    editIndex:undefined,
    contextRoot:'/',
    width:800,
    height:600
};
//初始化
vehDidSignal.init=function(ctxRoot,width,height){
	this.contextRoot = ctxRoot;
    this.width = width;
    this.height = height;
	this.initTreeMenu();
	this.initVehModeTree();
	this.initDidSignalGrid();
    this.initDialog();
};
vehDidSignal.addVehModeVerNode=function(){
    var row=$('#vehModeTree').treegrid('getSelected');
    var dd=$('#dd');
    var ff=$('#ff');
    if(row!=null && row['vehModeCode']!='all' && row['protocolVersion']=='all'){
        dd.dialog('setTitle',row['vehModeCode']+' 添加信号版本');
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
vehDidSignal.removeVehModeVerNode=function(){
    var row=$('#vehModeTree').treegrid('getSelected');
    if(row!=null && row['protocolVersion']!='all'){
        $.messager.confirm('确认', '删除车系版本吗?', function(r){
            if (r){
                $.post('/iov/metadata/vehDidSignal/removeVehProtocol',
                    {
                        'vehModeCode':row['vehModeCode'],
                        'protocolVersion':row['protocolVersion']
                    },function(data){
                        vehDidSignal.reloadVehModeTree();
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
vehDidSignal.reloadVehModeTree=function(){
    $('#vehModeTree').treegrid('reload');
};
vehDidSignal.initDialog=function(){
    $('#dd').dialog({
        title: '车系版本',
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
                     url:'/iov/metadata/vehDidSignal/addVehProtocolVersion',
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
                         vehDidSignal.reloadVehModeTree();
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
        width: 400,
        height: 345,
        closed: true,
        cache: false,
        modal: true,
        buttons:[{
            text:'保存',
            iconCls: 'icon-save',
            handler:function(){
                $.messager.progress();
                $('#ffConver').form('submit', {
                    url:'/iov/metadata/vehDidSignal/modifySignalConver',
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
                        $('#signalList').datagrid('reload');
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
    $('#ddRemark').dialog({
        title: '备注',
        width: 400,
        height: 345,
        closed: true,
        cache: false,
        modal: true,
        buttons:[{
            text:'保存',
            iconCls: 'icon-save',
            handler:function(){
                $.messager.progress();
                $('#ffRemark').form('submit', {
                    url:'/iov/metadata/vehDidSignal/modifyRemark',
                    onSubmit: function(){
                        var isValid = $(this).form('validate');
                        if (!isValid){
                            $.messager.progress('close');
                        }
                        return isValid;
                    },
                    success: function(){
                        $.messager.progress('close');
                        $('#ddRemark').dialog('close');
                        $('#signalList').datagrid('reload');
                    }
                });
            }
        },{
            text:'关闭',
            iconCls: 'icon-no',
            handler:function(){
                $('#ddRemark').dialog('close');
            }
        }]
    });
};
//树菜单
vehDidSignal.initTreeMenu=function(){
    var treeMenu=$('#treeMenu');
    treeMenu.menu({
        minWidth:120
    });
    treeMenu.menu('appendItem',{
        text: '增加',
        iconCls: 'icon-add',
        onclick: function(){
            vehDidSignal.addVehModeVerNode();
        }
    });
    treeMenu.menu('appendItem',{
        text: '删除',
        iconCls: 'icon-remove',
        onclick: function(){
            vehDidSignal.removeVehModeVerNode();
        }
    });
    treeMenu.menu('appendItem',{
        separator: true
    });
    treeMenu.menu('appendItem',{
        text: '刷新',
        iconCls: 'icon-reload',
        onclick: function(){
            vehDidSignal.reloadVehModeTree();
        }
    });
};
//列表树
vehDidSignal.initVehModeTree=function(){
	$('#vehModeTree').treegrid({
		url:'/iov/metadata/vehDidSignal/showVehModeTree',
		loadMsg:'数据加载中...',
		iconCls:'icon-save',
		width:280,
		height:vehDidSignal.height,
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
            var gd=$('#signalList');
            if(row['protocolVersion']!='all'){
                vehDidSignal.curVehModeCode=row['vehModeCode'];
                vehDidSignal.curProtocolVersion=row['protocolVersion'];
                $('#noSelectNode').attr("style","display:none");
                $('#nodeInfo').attr("style","display:block");
                gd.datagrid('getPanel').panel('setTitle',vehDidSignal.curVehModeCode+' Version: '+vehDidSignal.curProtocolVersion);
                gd.datagrid('load',{'vehModeCode':vehDidSignal.curVehModeCode,'protocolVersion':vehDidSignal.curProtocolVersion});
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
                vehDidSignal.addVehModeVerNode();
			}
		},{
			text:'删除',
			iconCls:'icon-remove',
			handler:function(){
                vehDidSignal.removeVehModeVerNode();			}
		},{
			text:'刷新',
			iconCls:'icon-reload',
			handler:function(){
                vehDidSignal.reloadVehModeTree();
			}
		}]
	});
};

//车系信号列表
vehDidSignal.initDidSignalGrid=function(){
	$('#signalList').datagrid({
		title:"车系信号配置",
		url:'/iov/metadata/vehDidSignal/list',
		loadMsg:'数据加载中...',
		width:vehDidSignal.width-350,
		height:vehDidSignal.height-18,
		nowrap: false,
		striped: true,
		collapsible:false,
        fitColumns:false,
        singleSelect: true,
		sortName: 'orderNum',
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
            {field:'didCode',title:'DID代码',sortable:true,width:80,halign:'center',editor:{'type':'combogrid','options':{
                panelWidth:300,
                panelHeight:400,
                idField:'id',
                textField:'code',
                mode:'remote',
                url:'/iov/metadata/did/queryDid',
                columns:[[
                    {field:'code',title:'代码',sortable:true,width:80,halign:'center'},
                    {field:'showNameEn',title:'英文名',sortable:true,width:120,halign:'center'},
                    {field:'showNameCn',title:'中文名',sortable:true,width:120,halign:'center'}
                ]]
            }}},
            {field:'didNameCn',title:'DID名称',sortable:false,width:90,halign:'center',formatter:function(value,row,index){
                return row['customDid']['showNameCn'];
            }},
            {field:'signalCode',title:'信号代码',sortable:false,width:90,halign:'center',editor:{'type':'combogrid','options':{
                panelWidth:300,
                panelHeight:400,
                idField:'id',
                textField:'code',
                mode:'remote',
                url:'/iov/metadata/signal/querySignal',
                columns:[[
                    {field:'code',title:'代码',sortable:true,width:80,halign:'center'},
                    {field:'showNameEn',title:'英文名',sortable:true,width:120,halign:'center'},
                    {field:'showNameCn',title:'中文名',sortable:true,width:120,halign:'center'}
                ]]
            }}},
            {field:'signalNameCn',title:'信号名称',sortable:false,width:90,halign:'center',formatter:function(value,row,index){
                return row['customSignal']['showNameCn'];
            }},
            {field:'orderNum',title:'序号',sortable:true,width:60,halign:'center',editor:'numberspinner'},
            {field:'length',title:'位长',sortable:false,width:60,halign:'center',editor:'numberbox'},
            {field:'bitOffset',title:'后偏移',sortable:false,width:60,halign:'center',editor:'numberbox'},
            {field:'preci',title:'精度',sortable:false,width:60,halign:'center',editor:'numberbox'},
            {field:'decodeMode',title:'类型',sortable:false,width:100,halign:'center',editor:{'type':'combobox','options':{
                panelWidth:'200',
                panelHeight:'150',
                valueField: 'value',
                textField: 'label',
                data:[
                    {label:'STRING',value:'string'},
                    {label:'SIGNED_INT',value:'signed_int'},
                    {label:'UNSIGNED_INT',value:'unsigned_int'},
                    {label:'SIGNED_LONG',value:'signed_long'},
                    {label:'UNSIGNED_LONG',value:'unsigned_long'},
                    {label:'DOUBLE',value:'double'}
                ]
            }}},
            {field:'signalConver',title:'公式',sortable:false,width:60,halign:'center',formatter:function(value,row,index){
                if(value.length > 1){
                    return "<a href=\"javascript:vehDidSignal.showSignalConver('"+row['id']+"','"+value+"')\"><span style='color:red;'>编辑</span></a>";
                }else{
                    return "<a href=\"javascript:vehDidSignal.showSignalConver('"+row['id']+"','"+value+"')\"><span style='color:green;'>添加</span></a>";
                }
            }},
            {field:'remark',title:'备注',sortable:false,width:60,halign:'center',formatter:function(value,row,index){
                if(value.length > 1){
                    return "<a href=\"javascript:vehDidSignal.showRemark('"+row['id']+"','"+value+"')\"><span style='color:red;'>编辑</span></a>";
                }else{
                    return "<a href=\"javascript:vehDidSignal.showRemark('"+row['id']+"','"+value+"')\"><span style='color:green;'>添加</span></a>";
                }
            }}
		]],
        toolbar:[{
            text:'增加',
            iconCls:'icon-add',
            handler:function(){
                $.post('/iov/metadata/vehDidSignal/add',
                    {
                        'vehModeCode':vehDidSignal.curVehModeCode,
                        'protocolVersion':vehDidSignal.curProtocolVersion
                    },function(data){
                        $('#signalList').datagrid('reload');
                    }
                );
            }
        },{
            text:'编辑',
            iconCls:'icon-edit',
            handler:function(){
                var gd=$("#signalList");
                var row=gd.datagrid("getSelected");
                if(row==null){
                    $.messager.alert('提示','请选择记录','info');
                    return;
                }
                var index=gd.datagrid("getRowIndex",row);
                if (vehDidSignal.editIndex != index){
                    if (vehDidSignal.endEditing()){
                        vehDidSignal.editIndex = index;
                        gd.datagrid('selectRow', index).datagrid('beginEdit',index);
                    } else {
                        setTimeout(function(){
                            gd.datagrid('selectRow', vehDidSignal.editIndex);
                        },0);
                    }
                }
            }
        },"-",{
            text:'保存',
            iconCls:'icon-save',
            handler:function(){
                var gd=$("#signalList");
                if(vehDidSignal.endEditing()){
                    gd.datagrid("acceptChanges");
                }
            }
        },"-",{
            text:'删除',
            iconCls:'icon-remove',
            handler:function(){
                var rec=$("#signalList").datagrid("getSelections");
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
                        $.post("/iov/metadata/vehDidSignal/remove",{"ids":ids},function(respData){
                            var gd=$("#signalList");
                            gd.datagrid("clearSelections");
                            gd.datagrid("reload");
                        });
                    }
                });
            }
        }],
        onDblClickRow: function(index, row){
            if (vehDidSignal.editIndex != index){
                if (vehDidSignal.endEditing()){
                    $('#signalList').datagrid('selectRow', index).datagrid('beginEdit',index);
                    vehDidSignal.editIndex = index;
                } else {
                    setTimeout(function(){
                        $('#signalList').datagrid('selectRow', vehDidSignal.editIndex);
                    },0);
                }
            }
        },
        onEndEdit: function(index,row,changes){
            var gd=$('#signalList');
            var editors = gd.datagrid('getEditors',index);
            var gdDid=$(editors[0].target).combogrid('grid');
            var gdSignal=$(editors[1].target).combogrid('grid');
            row['didCode'] = $(editors[0].target).combogrid('getText');
            if(gdDid!=null && gdDid.datagrid('getSelected')!=null){
                row['customDid']={};
                row['customDid']['id'] = gdDid.datagrid('getSelected')['id'];
                row['customDid']['showNameEn'] = gdDid.datagrid('getSelected')['showNameEn'];
                row['customDid']['showNameCn'] = gdDid.datagrid('getSelected')['showNameCn'];
            }
            row['signalCode'] = $(editors[1].target).combogrid('getText');
            if(gdSignal!=null && gdSignal.datagrid('getSelected')!=null){
                row['customSignal']={};
                row['customSignal']['id'] = gdSignal.datagrid('getSelected')['id'];
                row['customSignal']['showNameEn'] = gdSignal.datagrid('getSelected')['showNameEn'];
                row['customSignal']['showNameCn'] = gdSignal.datagrid('getSelected')['showNameCn'];
            }
            $.post('/iov/metadata/vehDidSignal/save',
                {
                 'id':row['id'],
                 'didId':row['customDid']['id'],
                 'signalId':row['customSignal']['id'],
                 'orderNum':row['orderNum'],
                 'length':row['length'],
                 'preci':row['preci'],
                 'bitOffset':row['bitOffset'],
                 'decodeMode':row['decodeMode']
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
vehDidSignal.endEditing=function(){
    var gd=$('#signalList');
    if (vehDidSignal.editIndex == undefined){
        return true
    }
    if (gd.datagrid('validateRow', vehDidSignal.editIndex)){
        gd.datagrid('endEdit', vehDidSignal.editIndex);
        vehDidSignal.editIndex = undefined;
        return true;
    }
    return false;
};
//编辑公式
vehDidSignal.showSignalConver=function(didSignalId,value){
    var dd=$('#ddConver');
    var ff=$('#ffConver');
    ff.form('load',{
        id:didSignalId,
        signalConver:$.base64.decode(value)
    });
    dd.dialog('open');
};
//编辑备注
vehDidSignal.showRemark=function(didSignalId,value){
    var dd=$('#ddRemark');
    var ff=$('#ffRemark');
    ff.form('load',{
        id:didSignalId,
        remark:$.base64.decode(value)
    });
    dd.dialog('open');
};