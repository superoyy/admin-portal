var eventTopic={
	curEventCode:'all',
    editIndex:undefined,
    contextRoot:'/',
    width:800,
    height:600
};
//初始化
eventTopic.init=function(ctxRoot,width,height){
	this.contextRoot = ctxRoot;
    this.width = width;
    this.height = height;
	this.initTreeMenu();
	this.initEventTree();
	this.initEventTopicGrid();
    this.initDialog();
};
//清空
eventTopic.clearEventTopic=function(){
    var row=$('#eventTree').treegrid('getSelected');
    if(row!=null && row['eventCode']!='all'){
        $.messager.confirm('确认', '清空 '+row['eventCode']+' 分发配置吗?', function(r){
            if (r){
                $.post('/iov/metadata/mpdEventTopic/clear',
                    {
                        'eventCode':row['eventCode']
                    },function(data){
                        eventTopic.reloadEventTree();
                        $('#noSelectNode').attr("style","display:block");
                        $('#nodeInfo').attr("style","display:none");
                    }
                );
            }
        });
    }else{
        $.messager.alert('提示','请选择事件节点!','info');
    }
};
eventTopic.reloadEventTree=function(){
    $('#eventTree').treegrid('reload');
};
eventTopic.initDialog=function(){
    $('#ddFilter').dialog({
        title: '过滤',
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
                $('#ffFilter').form('submit', {
                    url:'/iov/metadata/mpdEventTopic/modifyFilter',
                    onSubmit: function(){
                        var isValid = $(this).form('validate');
                        if (!isValid){
                            $.messager.progress('close');
                        }
                        return isValid;
                    },
                    success: function(){
                        $.messager.progress('close');
                        $('#ddFilter').dialog('close');
                        $('#eventTopicList').datagrid('reload');
                    }
                });
            }
        },{
            text:'关闭',
            iconCls: 'icon-no',
            handler:function(){
                $('#ddFilter').dialog('close');
            }
        }]
    });
    $('#ddAlias').dialog({
        title: '过滤',
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
                $('#ffAlias').form('submit', {
                    url:'/iov/metadata/mpdEventTopic/modifyAlias',
                    onSubmit: function(){
                        var isValid = $(this).form('validate');
                        if (!isValid){
                            $.messager.progress('close');
                        }
                        return isValid;
                    },
                    success: function(){
                        $.messager.progress('close');
                        $('#ddAlias').dialog('close');
                        $('#eventTopicList').datagrid('reload');
                    }
                });
            }
        },{
            text:'关闭',
            iconCls: 'icon-no',
            handler:function(){
                $('#ddAlias').dialog('close');
            }
        }]
    });
};

//树菜单
eventTopic.initTreeMenu=function(){
    var treeMenu=$('#treeMenu');
    treeMenu.menu({
        minWidth:120
    });
    treeMenu.menu('appendItem',{
        text: '清空',
        iconCls: 'icon-remove',
        onclick: function(){
            eventTopic.clearEventTopic();
        }
    });
    treeMenu.menu('appendItem',{
        separator: true
    });
    treeMenu.menu('appendItem',{
        text: '刷新',
        iconCls: 'icon-reload',
        onclick: function(){
            eventTopic.reloadEventTree();
        }
    });
};
//列表树
eventTopic.initEventTree=function(){
	$('#eventTree').treegrid({
		url:'/iov/metadata/mpdEventTopic/showEventTree',
		loadMsg:'数据加载中...',
		iconCls:'icon-save',
		width:350,
		height:eventTopic.height,
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
            var gd=$('#eventTopicList');
            if(row['eventCode']!='all'){
                eventTopic.curEventCode=row['eventCode'];
                $('#noSelectNode').attr("style","display:none");
                $('#nodeInfo').attr("style","display:block");
                gd.datagrid('getPanel').panel('setTitle',eventTopic.curEventCode);
                gd.datagrid('load',{'eventCode':eventTopic.curEventCode});
            }else{
                $('#noSelectNode').attr("style","display:block");
                $('#nodeInfo').attr("style","display:none");
            }
		},
		onContextMenu:function(e,node){
			e.preventDefault();
			$('#eventTree').treegrid('select', node.id);
			$('#treeMenu').menu('show', {
				left: e.pageX,
				top: e.pageY
			});
		},
		toolbar:[{
			text:'清空',
			iconCls:'icon-remove',
			handler:function(){
                eventTopic.clearEventTopic();
            }
		},{
			text:'刷新',
			iconCls:'icon-reload',
			handler:function(){
                eventTopic.reloadEventTree();
			}
		}]
	});
};
//事件分发列表
eventTopic.initEventTopicGrid=function(){
	$('#eventTopicList').datagrid({
		title:"事件分发配置",
		url:'/iov/metadata/mpdEventTopic/list',
		loadMsg:'数据加载中...',
		width:eventTopic.width-420,
		height:eventTopic.height-18,
		nowrap: false,
		striped: true,
		collapsible:false,
        fitColumns:false,
        singleSelect: true,
		sortName: 'id',
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
            {field:'mpdTopicName',title:'分发队列',sortable:true,width:90,halign:'center',editor:{'type':'combogrid','options':{
                panelWidth:300,
                panelHeight:400,
                idField:'id',
                textField:'topicName',
                mode:'remote',
                url:'/iov/metadata/mpdTopic/queryTopic',
                columns:[[
                    {field:'topicName',title:'名称',sortable:true,width:80,halign:'center'},
                    {field:'status',title:'状态',sortable:true,width:80,halign:'center'}
                ]]
            }},formatter:function(value,row,index){
                return row['mpdTopic']['topicName'];
            }},
            {field:'signalFilter',title:'过滤字段',sortable:false,width:300,halign:'center',formatter:function(value,row,index){
                if(value.length > 1){
                    return "<a href=\"javascript:eventTopic.showFilter('"+row['id']+"','"+value+"')\"><span style='color:red;'>编辑</span></a>";
                }else{
                    return "<a href=\"javascript:eventTopic.showFilter('"+row['id']+"','"+value+"')\"><span style='color:green;'>添加</span></a>";
                }
            }},
            {field:'alias',title:'字段别名',sortable:false,width:300,halign:'center',formatter:function(value,row,index){
                if(value.length > 1){
                    return "<a href=\"javascript:eventTopic.showAlias('"+row['id']+"','"+value+"')\"><span style='color:red;'>编辑</span></a>";
                }else{
                    return "<a href=\"javascript:eventTopic.showAlias('"+row['id']+"','"+value+"')\"><span style='color:green;'>添加</span></a>";
                }
            }}
		]],
        toolbar:[{
            text:'增加',
            iconCls:'icon-add',
            handler:function(){
                $.post('/iov/metadata/mpdEventTopic/add',
                    {
                        'eventCode':eventTopic.curEventCode
                    },function(data){
                        $('#eventTopicList').datagrid('reload');
                    }
                );
            }
        },{
            text:'编辑',
            iconCls:'icon-edit',
            handler:function(){
                var gd=$("#eventTopicList");
                var row=gd.datagrid("getSelected");
                if(row==null){
                    $.messager.alert('提示','请选择记录','info');
                    return;
                }
                var index=gd.datagrid("getRowIndex",row);
                if (eventTopic.editIndex != index){
                    if (eventTopic.endEditing()){
                        eventTopic.editIndex = index;
                        gd.datagrid('selectRow', index).datagrid('beginEdit',index);
                    } else {
                        setTimeout(function(){
                            gd.datagrid('selectRow', eventTopic.editIndex);
                        },0);
                    }
                }
            }
        },"-",{
            text:'保存',
            iconCls:'icon-save',
            handler:function(){
                var gd=$("#eventTopicList");
                if(eventTopic.endEditing()){
                    gd.datagrid("acceptChanges");
                }
            }
        },"-",{
            text:'删除',
            iconCls:'icon-remove',
            handler:function(){
                var rec=$("#eventTopicList").datagrid("getSelections");
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
                        $.post("/iov/metadata/mpdEventTopic/remove",{"ids":ids},function(respData){
                            var gd=$("#eventTopicList");
                            gd.datagrid("clearSelections");
                            gd.datagrid("reload");
                            eventTopic.reloadEventTree();
                        });
                    }
                });
            }
        }],
        onDblClickRow: function(index, row){
            if (eventTopic.editIndex != index){
                if (eventTopic.endEditing()){
                    $('#eventTopicList').datagrid('selectRow', index).datagrid('beginEdit',index);
                    eventTopic.editIndex = index;
                } else {
                    setTimeout(function(){
                        $('#eventTopicList').datagrid('selectRow', eventTopic.editIndex);
                    },0);
                }
            }
        },
        onEndEdit: function(index,row,changes){
            var gd=$('#eventTopicList');
            var editors = gd.datagrid('getEditors',index);
            var gdTopic=$(editors[0].target).combogrid('grid');
            if(gdTopic!=null && gdTopic.datagrid('getSelected')!=null){
                row['mpdTopic']={};
                row['mpdTopic']['id'] = gdTopic.datagrid('getSelected')['id'];
                row['mpdTopic']['topicName'] = gdTopic.datagrid('getSelected')['topicName'];
            }
            $.post('/iov/metadata/mpdEventTopic/save',
                {
                 'id':row['id'],
                 'mpdTopicId':row['mpdTopic']['id']
                },
                function(respData) {
                    gd.datagrid('unselectAll');
                    gd.datagrid('reload');
                    eventTopic.reloadEventTree();
                }
            );
        }
	});
};
//完成编辑
eventTopic.endEditing=function(){
    var gd=$('#eventTopicList');
    if (eventTopic.editIndex == undefined){
        return true
    }
    if (gd.datagrid('validateRow', eventTopic.editIndex)){
        gd.datagrid('endEdit', eventTopic.editIndex);
        eventTopic.editIndex = undefined;
        return true;
    }
    return false;
};
//编辑过滤
eventTopic.showFilter=function(eventTopicId,value){
    var dd=$('#ddFilter');
    var ff=$('#ffFilter');
    ff.form('load',{
        id:eventTopicId,
        valFilter:$.base64.decode(value)
    });
    dd.dialog('open');
};
//编辑别名
eventTopic.showAlias=function(eventTopicId,value){
    var dd=$('#ddAlias');
    var ff=$('#ffAlias');
    ff.form('load',{
        id:eventTopicId,
        valAlias:$.base64.decode(value)
    });
    dd.dialog('open');
};
