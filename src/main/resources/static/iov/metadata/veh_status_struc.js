var vehStatusStruc={
	curVehModeCode:'all',
    curProtocolVersion:'all',
    editIndex:undefined,
    contextRoot:'/',
    width:800,
    height:600
};
//初始化
vehStatusStruc.init=function(ctxRoot,width,height){
	this.contextRoot = ctxRoot;
    this.width = width;
    this.height = height;
	this.initTreeMenu();
	this.initVehModeTree();
	this.initStatusStrucGrid();
    this.initDialog();
};
vehStatusStruc.addVehModeVerNode=function(){
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
vehStatusStruc.removeVehModeVerNode=function(){
    var row=$('#vehModeTree').treegrid('getSelected');
    if(row!=null && row['protocolVersion']!='all'){
        $.messager.confirm('确认', '删除车系版本吗?', function(r){
            if (r){
                $.post('/iov/metadata/vehStatusStruc/removeVehProtocol',
                    {
                        'vehModeCode':row['vehModeCode'],
                        'protocolVersion':row['protocolVersion']
                    },function(data){
                        vehStatusStruc.reloadVehModeTree();
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
vehStatusStruc.reloadVehModeTree=function(){
    $('#vehModeTree').treegrid('reload');
};
vehStatusStruc.initDialog=function(){
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
                     url:'/iov/metadata/vehStatusStruc/addVehProtocolVersion',
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
                         vehStatusStruc.reloadVehModeTree();
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
                    url:'/iov/metadata/vehStatusStruc/modifyValConver',
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
                        $('#statusStrucList').datagrid('reload');
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
vehStatusStruc.initTreeMenu=function(){
    var treeMenu=$('#treeMenu');
    treeMenu.menu({
        minWidth:120
    });
    treeMenu.menu('appendItem',{
        text: '增加',
        iconCls: 'icon-add',
        onclick: function(){
            vehStatusStruc.addVehModeVerNode();
        }
    });
    treeMenu.menu('appendItem',{
        text: '删除',
        iconCls: 'icon-remove',
        onclick: function(){
            vehStatusStruc.removeVehModeVerNode();
        }
    });
    treeMenu.menu('appendItem',{
        separator: true
    });
    treeMenu.menu('appendItem',{
        text: '刷新',
        iconCls: 'icon-reload',
        onclick: function(){
            vehStatusStruc.reloadVehModeTree();
        }
    });
};
//列表树
vehStatusStruc.initVehModeTree=function(){
	$('#vehModeTree').treegrid({
		url:'/iov/metadata/vehStatusStruc/showVehModeTree',
		loadMsg:'数据加载中...',
		iconCls:'icon-save',
		width:280,
		height:vehStatusStruc.height,
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
            var gd=$('#statusStrucList');
            if(row['protocolVersion']!='all'){
                vehStatusStruc.curVehModeCode=row['vehModeCode'];
                vehStatusStruc.curProtocolVersion=row['protocolVersion'];
                $('#noSelectNode').attr("style","display:none");
                $('#nodeInfo').attr("style","display:block");
                gd.datagrid('getPanel').panel('setTitle',vehStatusStruc.curVehModeCode+' Version: '+vehStatusStruc.curProtocolVersion);
                gd.datagrid('load',{'vehModeCode':vehStatusStruc.curVehModeCode,'protocolVersion':vehStatusStruc.curProtocolVersion});
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
                vehStatusStruc.addVehModeVerNode();
			}
		},{
			text:'删除',
			iconCls:'icon-remove',
			handler:function(){
                vehStatusStruc.removeVehModeVerNode();			}
		},{
			text:'刷新',
			iconCls:'icon-reload',
			handler:function(){
                vehStatusStruc.reloadVehModeTree();
			}
		}]
	});
};

//车系信号列表
vehStatusStruc.initStatusStrucGrid=function(){
	$('#statusStrucList').datagrid({
		title:"车况结构配置",
		url:'/iov/metadata/vehStatusStruc/list',
		loadMsg:'数据加载中...',
		width:vehStatusStruc.width-350,
		height:vehStatusStruc.height-18,
		nowrap: false,
		striped: true,
		collapsible:false,
        fitColumns:false,
        singleSelect: true,
		sortName: 'nodeOrder',
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
            {field:'statusCode',title:'车况代码',sortable:true,width:80,halign:'center',editor:{'type':'combogrid','options':{
                panelWidth:300,
                panelHeight:400,
                idField:'id',
                textField:'code',
                mode:'remote',
                url:'/iov/metadata/status/queryStatus',
                columns:[[
                    {field:'code',title:'代码',sortable:true,width:80,halign:'center'},
                    {field:'showNameEn',title:'英文名',sortable:true,width:120,halign:'center'},
                    {field:'showNameCn',title:'中文名',sortable:true,width:120,halign:'center'}
                ]]
            }}},
            {field:'statusNameCn',title:'名称',sortable:false,width:90,halign:'center',formatter:function(value,row,index){
                return row['globalStatus']['showNameCn'];
            }},
            {field:'upStatusCode',title:'上级车况代码',sortable:true,width:100,halign:'center',editor:{'type':'combogrid','options':{
                panelWidth:300,
                panelHeight:400,
                idField:'id',
                textField:'code',
                mode:'remote',
                url:'/iov/metadata/status/queryStatus',
                columns:[[
                    {field:'code',title:'代码',sortable:true,width:80,halign:'center'},
                    {field:'showNameEn',title:'英文名',sortable:true,width:120,halign:'center'},
                    {field:'showNameCn',title:'中文名',sortable:true,width:120,halign:'center'}
                ]]
            }}},
            {field:'upStatusNameCn',title:'名称',sortable:false,width:90,halign:'center',formatter:function(value,row,index){
                return row['upGlobalStatus']['showNameCn'];
            }},
            {field:'didCode',title:'DID代码',sortable:true,width:90,halign:'center',editor:{'type':'combogrid','options':{
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
            {field:'signalIndex',title:'信号位置',sortable:true,width:60,halign:'center',editor:'numberbox'},
            {field:'nodeOrder',title:'排序号',sortable:true,width:60,halign:'center',editor:'numberbox'},
            {field:'nodeVal',title:'默认值',sortable:false,width:80,halign:'center',editor:'text'},
            {field:'nodeType',title:'节点类型',sortable:false,width:60,halign:'center',editor:{'type':'combobox','options':{
                panelWidth:'200',
                panelHeight:'150',
                valueField: 'value',
                textField: 'label',
                data:[
                    {label:'结构',value:'0'},
                    {label:'值',value:'1'}
                ]
            }}},
            {field:'valConver',title:'公式',sortable:false,width:60,halign:'center',formatter:function(value,row,index){
                if(value.length > 1){
                    return "<a href=\"javascript:vehStatusStruc.showValConver('"+row['id']+"','"+value+"')\"><span style='color:red;'>编辑</span></a>";
                }else{
                    return "<a href=\"javascript:vehStatusStruc.showValConver('"+row['id']+"','"+value+"')\"><span style='color:green;'>添加</span></a>";
                }
            }}
		]],
        toolbar:[{
            text:'增加',
            iconCls:'icon-add',
            handler:function(){
                $.post('/iov/metadata/vehStatusStruc/add',
                    {
                        'vehModeCode':vehStatusStruc.curVehModeCode,
                        'protocolVersion':vehStatusStruc.curProtocolVersion
                    },function(data){
                        $('#statusStrucList').datagrid('reload');
                    }
                );
            }
        },{
            text:'编辑',
            iconCls:'icon-edit',
            handler:function(){
                var gd=$("#statusStrucList");
                var row=gd.datagrid("getSelected");
                if(row==null){
                    $.messager.alert('提示','请选择记录','info');
                    return;
                }
                var index=gd.datagrid("getRowIndex",row);
                if (vehStatusStruc.editIndex != index){
                    if (vehStatusStruc.endEditing()){
                        vehStatusStruc.editIndex = index;
                        gd.datagrid('selectRow', index).datagrid('beginEdit',index);
                    } else {
                        setTimeout(function(){
                            gd.datagrid('selectRow', vehStatusStruc.editIndex);
                        },0);
                    }
                }
            }
        },"-",{
            text:'保存',
            iconCls:'icon-save',
            handler:function(){
                var gd=$("#statusStrucList");
                if(vehStatusStruc.endEditing()){
                    gd.datagrid("acceptChanges");
                }
            }
        },"-",{
            text:'删除',
            iconCls:'icon-remove',
            handler:function(){
                var rec=$("#statusStrucList").datagrid("getSelections");
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
                        $.post("/iov/metadata/vehStatusStruc/remove",{"ids":ids},function(respData){
                            var gd=$("#statusStrucList");
                            gd.datagrid("clearSelections");
                            gd.datagrid("reload");
                        });
                    }
                });
            }
        }],
        onDblClickRow: function(index, row){
            if (vehStatusStruc.editIndex != index){
                if (vehStatusStruc.endEditing()){
                    $('#statusStrucList').datagrid('selectRow', index).datagrid('beginEdit',index);
                    vehStatusStruc.editIndex = index;
                } else {
                    setTimeout(function(){
                        $('#statusStrucList').datagrid('selectRow', vehStatusStruc.editIndex);
                    },0);
                }
            }
        },
        onEndEdit: function(index,row,changes){
            var gd=$('#statusStrucList');
            var editors = gd.datagrid('getEditors',index);
            var gdStatus=$(editors[0].target).combogrid('grid');
            row['statusCode'] = $(editors[0].target).combogrid('getText');
            if(gdStatus!=null && gdStatus.datagrid('getSelected')!=null){
                row['globalStatus']={};
                row['globalStatus']['id'] = gdStatus.datagrid('getSelected')['id'];
                row['globalStatus']['showNameEn'] = gdStatus.datagrid('getSelected')['showNameEn'];
                row['globalStatus']['showNameCn'] = gdStatus.datagrid('getSelected')['showNameCn'];
            }
            var gdUpStatus=$(editors[1].target).combogrid('grid');
            row['upStatusCode'] = $(editors[1].target).combogrid('getText');
            if(gdUpStatus!=null && gdUpStatus.datagrid('getSelected')!=null){
                row['upGlobalStatus']={};
                row['upGlobalStatus']['id'] = gdUpStatus.datagrid('getSelected')['id'];
                row['upGlobalStatus']['showNameEn'] = gdUpStatus.datagrid('getSelected')['showNameEn'];
                row['upGlobalStatus']['showNameCn'] = gdUpStatus.datagrid('getSelected')['showNameCn'];
            }
            row['didCode'] = $(editors[2].target).combogrid('getText');
            $.post('/iov/metadata/vehStatusStruc/save',
                {
                 'id':row['id'],
                 'statusId':row['globalStatus']['id'],
                 'upStatusId':row['upGlobalStatus']['id'],
                 'nodeType':row['nodeType'],
                 'nodeOrder':row['nodeOrder'],
                 'nodeVal':row['nodeVal'],
                 'didCode':row['didCode'],
                 'signalIndex':row['signalIndex']
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
vehStatusStruc.endEditing=function(){
    var gd=$('#statusStrucList');
    if (vehStatusStruc.editIndex == undefined){
        return true
    }
    if (gd.datagrid('validateRow', vehStatusStruc.editIndex)){
        gd.datagrid('endEdit', vehStatusStruc.editIndex);
        vehStatusStruc.editIndex = undefined;
        return true;
    }
    return false;
};
//编辑公式
vehStatusStruc.showValConver=function(statusStrucId,value){
    var dd=$('#ddConver');
    var ff=$('#ffConver');
    ff.form('load',{
        id:statusStrucId,
        valConver:$.base64.decode(value)
    });
    dd.dialog('open');
};
