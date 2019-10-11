var vehEcu={
	curVehModeCode:'all',
    editIndex:undefined,
    contextRoot:'/',
    width:800,
    height:600
};
//初始化
vehEcu.init=function(ctxRoot,width,height){
	this.contextRoot = ctxRoot;
    this.width = width;
    this.height = height;
	this.initTreeMenu();
	this.initVehModeTree();
	this.iniVehEcuGrid();
};
//清空
vehEcu.clearVehEcu=function(){
    var row=$('#vehModeTree').treegrid('getSelected');
    if(row!=null && row['vehModeCode']!='all'){
        $.messager.confirm('确认', '清空 '+row['vehModeCode']+' ECU吗?', function(r){
            if (r){
                $.post('/iov/metadata/vehEcu/clear',
                    {
                        'vehModeCode':row['vehModeCode']
                    },function(){
                        vehEcu.reloadVehModeTree();
                        $('#noSelectNode').attr("style","display:block");
                        $('#nodeInfo').attr("style","display:none");
                    }
                );
            }
        });
    }else{
        $.messager.alert('提示','请选择车系节点!','info');
    }
};
vehEcu.reloadVehModeTree=function(){
    $('#vehModeTree').treegrid('reload');
};
//树菜单
vehEcu.initTreeMenu=function(){
    var treeMenu=$('#treeMenu');
    treeMenu.menu({
        minWidth:120
    });
    treeMenu.menu('appendItem',{
        text: '清空',
        iconCls: 'icon-remove',
        onclick: function(){
            vehEcu.clearVehEcu();
        }
    });
    treeMenu.menu('appendItem',{
        separator: true
    });
    treeMenu.menu('appendItem',{
        text: '刷新',
        iconCls: 'icon-reload',
        onclick: function(){
            vehEcu.reloadVehModeTree();
        }
    });
};
//列表树
vehEcu.initVehModeTree=function(){
	$('#vehModeTree').treegrid({
		url:'/iov/metadata/vehEcu/showVehModeTree',
		loadMsg:'数据加载中...',
		iconCls:'icon-save',
		width:280,
		height:vehEcu.height,
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
            var gd=$('#vehEcuList');
            if(row['vehModeCode']!='all'){
                vehEcu.curVehModeCode=row['vehModeCode'];
                $('#noSelectNode').attr("style","display:none");
                $('#nodeInfo').attr("style","display:block");
                gd.datagrid('getPanel').panel('setTitle',vehEcu.curVehModeCode);
                gd.datagrid('load',{'vehModeCode':vehEcu.curVehModeCode});
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
			text:'清空',
			iconCls:'icon-remove',
			handler:function(){
                vehEcu.clearVehEcu();
            }
		},{
			text:'刷新',
			iconCls:'icon-reload',
			handler:function(){
                vehEcu.reloadVehModeTree();
			}
		}]
	});
};
//车系Ecu列表
vehEcu.iniVehEcuGrid=function(){
	$('#vehEcuList').datagrid({
		title:"车系DTC配置",
		url:'/iov/metadata/vehEcu/list',
		loadMsg:'数据加载中...',
		width:vehEcu.width-350,
		height:vehEcu.height-18,
		nowrap: false,
		striped: true,
		collapsible:false,
        fitColumns:false,
        singleSelect: true,
		sortName: 'ecuCode',
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
            {field:'ecuCode',title:'ECU代码',sortable:true,width:90,halign:'center',editor:{'type':'combogrid','options':{
                panelWidth:300,
                panelHeight:400,
                idField:'id',
                textField:'code',
                mode:'remote',
                url:'/iov/metadata/ecu/queryEcu',
                columns:[[
                    {field:'code',title:'代码',sortable:true,width:80,halign:'center'},
                    {field:'showNameEn',title:'英文名',sortable:true,width:120,halign:'center'},
                    {field:'showNameCn',title:'中文名',sortable:true,width:120,halign:'center'}
                ]]
            }}},
            {field:'ecuNameCn',title:'ECU名称',sortable:false,width:90,halign:'center',formatter:function(value,row,index){
                return row['customEcu']['showNameCn'];
            }},
            {field:'scoreFlag',title:'打分',sortable:false,width:100,halign:'center',editor:{'type':'combobox','options':{
                panelWidth:'200',
                panelHeight:'150',
                valueField: 'value',
                textField: 'label',
                data:[
                    {label:'是',value:'0'},
                    {label:'否',value:'1'}
                ]
            }},formatter:function(value,row,index){
                if(value=='0'){
                    return '是';
                }else if(value=='1'){
                    return '否';
                }
            }},
            {field:'scoreTotal',title:'总分',sortable:false,width:60,halign:'center',editor:'numberbox'},
            {field:'remark',title:'备注',sortable:false,width:60,halign:'center',editor:'text'}
		]],
        toolbar:[{
            text:'增加',
            iconCls:'icon-add',
            handler:function(){
                $.post('/iov/metadata/vehEcu/add',
                    {
                        'vehModeCode':vehEcu.curVehModeCode
                    },function(){
                        $('#vehEcuList').datagrid('reload');
                    }
                );
            }
        },{
            text:'编辑',
            iconCls:'icon-edit',
            handler:function(){
                var gd=$("#vehEcuList");
                var row=gd.datagrid("getSelected");
                if(row==null){
                    $.messager.alert('提示','请选择记录','info');
                    return;
                }
                var index=gd.datagrid("getRowIndex",row);
                if (vehEcu.editIndex != index){
                    if (vehEcu.endEditing()){
                        vehEcu.editIndex = index;
                        gd.datagrid('selectRow', index).datagrid('beginEdit',index);
                    } else {
                        setTimeout(function(){
                            gd.datagrid('selectRow', vehEcu.editIndex);
                        },0);
                    }
                }
            }
        },"-",{
            text:'保存',
            iconCls:'icon-save',
            handler:function(){
                var gd=$("#vehEcuList");
                if(vehEcu.endEditing()){
                    gd.datagrid("acceptChanges");
                }
            }
        },"-",{
            text:'删除',
            iconCls:'icon-remove',
            handler:function(){
                var rec=$("#vehEcuList").datagrid("getSelections");
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
                        $.post("/iov/metadata/vehEcu/remove",{"ids":ids},function(){
                            var gd=$("#vehEcuList");
                            gd.datagrid("clearSelections");
                            gd.datagrid("reload");
                        });
                    }
                });
            }
        }],
        onDblClickRow: function(index, row){
            if (vehEcu.editIndex != index){
                if (vehEcu.endEditing()){
                    $('#vehEcuList').datagrid('selectRow', index).datagrid('beginEdit',index);
                    vehEcu.editIndex = index;
                } else {
                    setTimeout(function(){
                        $('#vehEcuList').datagrid('selectRow', vehEcu.editIndex);
                    },0);
                }
            }
        },
        onEndEdit: function(index,row,changes){
            var gd=$('#vehEcuList');
            var editors = gd.datagrid('getEditors',index);
            var gdEcu=$(editors[0].target).combogrid('grid');
            row['ecuCode'] = $(editors[0].target).combogrid('getText');
            if(gdEcu!=null && gdEcu.datagrid('getSelected')!=null){
                row['customEcu']={};
                row['customEcu']['id'] = gdEcu.datagrid('getSelected')['id'];
                row['customEcu']['showNameEn'] = gdEcu.datagrid('getSelected')['showNameEn'];
                row['customEcu']['showNameCn'] = gdEcu.datagrid('getSelected')['showNameCn'];
            }
            $.post('/iov/metadata/vehEcu/save',
                {
                 'id':row['id'],
                 'ecuId':row['customEcu']['id'],
                 'scoreFlag':row['scoreFlag'],
                 'scoreTotal':row['scoreTotal'],
                 'remark':row['remark']
                },
                function() {
                    gd.datagrid('unselectAll');
                    gd.datagrid('reload');
                }
            );
        }
	});
};
//完成编辑
vehEcu.endEditing=function(){
    var gd=$('#vehEcuList');
    if (vehEcu.editIndex == undefined){
        return true
    }
    if (gd.datagrid('validateRow', vehEcu.editIndex)){
        gd.datagrid('endEdit', vehEcu.editIndex);
        vehEcu.editIndex = undefined;
        return true;
    }
    return false;
};