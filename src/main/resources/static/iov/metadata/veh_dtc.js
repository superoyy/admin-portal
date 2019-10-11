var vehDtc={
	curVehModeCode:'all',
    editIndex:undefined,
    contextRoot:'/',
    width:800,
    height:600
};
//初始化
vehDtc.init=function(ctxRoot,width,height){
	this.contextRoot = ctxRoot;
    this.width = width;
    this.height = height;
	this.initTreeMenu();
	this.initVehModeTree();
	this.initDtcGrid();
    this.initDialog();
};
//清空
vehDtc.clearVehModeDtc=function(){
    var row=$('#vehModeTree').treegrid('getSelected');
    if(row!=null && row['vehModeCode']!='all'){
        $.messager.confirm('确认', '清空 '+row['vehModeCode']+' DTC吗?', function(r){
            if (r){
                $.post('/iov/metadata/vehDtc/clear',
                    {
                        'vehModeCode':row['vehModeCode']
                    },function(data){
                        vehDtc.reloadVehModeTree();
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
vehDtc.reloadVehModeTree=function(){
    $('#vehModeTree').treegrid('reload');
};
vehDtc.initDialog=function(){
    $('#dd').dialog({
        title: '车系DTC导入',
        width: 400,
        height: 160,
        closed: true,
        cache: false,
        modal: true,
        buttons:[{
             text:'上传',
             iconCls: 'icon-undo',
             handler:function(){
                 var formData = new FormData($('#ff')[0]);
                 if($('#dtcFile').filebox('getValue').lastIndexOf('.xlsx')==-1){
                     $('#importMsg').html('请选择.xlsx文件!');
                     return;
                 }
                 $.messager.progress();
                 //将form数据传递给后台处理 contentType 必须设置为false,否则chrome和firefox不兼容
                 $.ajax({
                     url: "/iov/metadata/vehDtc/import",
                     type: 'POST',
                     data: formData,
                     async: false,
                     cache: false,
                     contentType: false,
                     processData: false,
                     success: function (respData) {
                         $('#importMsg').html(respData['message']);
                         $.messager.progress('close');
                     },
                     error: function (respData) {
                         $.messager.progress('close');
                     }
                 });
             }
          },{
            text:'关闭',
            iconCls: 'icon-no',
            handler:function(){
                vehDtc.reloadVehModeTree();
                $('#dtcList').datagrid('reload');
                $('#dd').dialog('close');

            }
        }]
    });
    $('#dtcFile').filebox({
        buttonText: '选择文件',
        required:true
    });
};
//树菜单
vehDtc.initTreeMenu=function(){
    var treeMenu=$('#treeMenu');
    treeMenu.menu({
        minWidth:120
    });
    treeMenu.menu('appendItem',{
        text: '导入',
        iconCls: 'icon-add',
        onclick: function(){
            vehDtc.showImport();
        }
    });
    treeMenu.menu('appendItem',{
        text: '清空',
        iconCls: 'icon-remove',
        onclick: function(){
            vehDtc.clearVehModeDtc();
        }
    });
    treeMenu.menu('appendItem',{
        separator: true
    });
    treeMenu.menu('appendItem',{
        text: '刷新',
        iconCls: 'icon-reload',
        onclick: function(){
            vehDtc.reloadVehModeTree();
        }
    });
};
//列表树
vehDtc.initVehModeTree=function(){
	$('#vehModeTree').treegrid({
		url:'/iov/metadata/vehDtc/showVehModeTree',
		loadMsg:'数据加载中...',
		iconCls:'icon-save',
		width:280,
		height:vehDtc.height,
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
            var gd=$('#dtcList');
            if(row['vehModeCode']!='all'){
                vehDtc.curVehModeCode=row['vehModeCode'];
                $('#noSelectNode').attr("style","display:none");
                $('#nodeInfo').attr("style","display:block");
                gd.datagrid('getPanel').panel('setTitle',vehDtc.curVehModeCode);
                gd.datagrid('load',{'vehModeCode':vehDtc.curVehModeCode});
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
			text:'导入',
			iconCls:'icon-add',
			handler:function(){
                vehDtc.showImport();
			}
		},{
			text:'清空',
			iconCls:'icon-remove',
			handler:function(){
                vehDtc.clearVehModeDtc();
            }
		},{
			text:'刷新',
			iconCls:'icon-reload',
			handler:function(){
                vehDtc.reloadVehModeTree();
			}
		}]
	});
};
vehDtc.showImport=function(){
    var dd=$('#dd');
    var ff=$('#ff');
    var row=$('#vehModeTree').treegrid('getSelected');
    if(row!=null && row['vehModeCode']!='all'){
        ff.form('load',{
            vehModeCode:vehDtc.curVehModeCode
        });
        dd.dialog('setTitle',vehDtc.curVehModeCode+' DTC数据导入');
        dd.dialog('open');
    }else{
        $.messager.alert('提示','请选择车系节点!','info');
    }
};
//车系DTC列表
vehDtc.initDtcGrid=function(){
	$('#dtcList').datagrid({
		title:"车系DTC配置",
		url:'/iov/metadata/vehDtc/list',
		loadMsg:'数据加载中...',
		width:vehDtc.width-350,
		height:vehDtc.height-18,
		nowrap: false,
		striped: true,
		collapsible:false,
        fitColumns:false,
        singleSelect: true,
		sortName: 'code',
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
            {field:'code',title:'代码',sortable:true,width:80,halign:'center',editor:'text'},
            {field:'level',title:'等级',sortable:false,width:100,halign:'center',editor:{'type':'combobox','options':{
                panelWidth:'200',
                panelHeight:'150',
                valueField: 'value',
                textField: 'label',
                data:[
                    {label:'一级',value:'1'},
                    {label:'二级',value:'2'},
                    {label:'三级',value:'3'}
                ]
            }}},
            {field:'failureType',title:'类型',sortable:false,width:60,halign:'center',editor:'text'},
            {field:'describCh',title:'中文名',sortable:false,width:100,halign:'center',editor:'text'},
            {field:'describEn',title:'英文名',sortable:false,width:100,halign:'center',editor:'text'},
            {field:'dispFlag',title:'显示',sortable:false,width:60,halign:'center',editor:{'type':'combobox','options':{
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
            {field:'overruledFlag',title:'评分',sortable:false,width:60,halign:'center',editor:{'type':'combobox','options':{
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
            {field:'remark',title:'备注',sortable:false,width:60,halign:'center',formatter:function(value,row,index){
                if(value.length>1){
                    return "<span style='color:red;'>编辑</span>";
                }
            }}
		]],
        toolbar:[{
            text:'增加',
            iconCls:'icon-add',
            handler:function(){
                $.post('/iov/metadata/vehDtc/add',
                    {
                        'vehModeCode':vehDtc.curVehModeCode
                    },function(data){
                        $('#dtcList').datagrid('reload');
                    }
                );
            }
        },{
            text:'编辑',
            iconCls:'icon-edit',
            handler:function(){
                var gd=$("#dtcList");
                var row=gd.datagrid("getSelected");
                if(row==null){
                    $.messager.alert('提示','请选择记录','info');
                    return;
                }
                var index=gd.datagrid("getRowIndex",row);
                if (vehDtc.editIndex != index){
                    if (vehDtc.endEditing()){
                        vehDtc.editIndex = index;
                        gd.datagrid('selectRow', index).datagrid('beginEdit',index);
                    } else {
                        setTimeout(function(){
                            gd.datagrid('selectRow', vehDtc.editIndex);
                        },0);
                    }
                }
            }
        },"-",{
            text:'保存',
            iconCls:'icon-save',
            handler:function(){
                var gd=$("#dtcList");
                if(vehDtc.endEditing()){
                    gd.datagrid("acceptChanges");
                }
            }
        },"-",{
            text:'删除',
            iconCls:'icon-remove',
            handler:function(){
                var rec=$("#dtcList").datagrid("getSelections");
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
                        $.post("/iov/metadata/vehDtc/remove",{"ids":ids},function(respData){
                            var gd=$("#dtcList");
                            gd.datagrid("clearSelections");
                            gd.datagrid("reload");
                        });
                    }
                });
            }
        }],
        onDblClickRow: function(index, row){
            if (vehDtc.editIndex != index){
                if (vehDtc.endEditing()){
                    $('#dtcList').datagrid('selectRow', index).datagrid('beginEdit',index);
                    vehDtc.editIndex = index;
                } else {
                    setTimeout(function(){
                        $('#dtcList').datagrid('selectRow', vehDtc.editIndex);
                    },0);
                }
            }
        },
        onEndEdit: function(index,row,changes){
            var gd=$('#dtcList');
            var editors = gd.datagrid('getEditors',index);
            var gdEcu=$(editors[0].target).combogrid('grid');
            row['ecuCode'] = $(editors[0].target).combogrid('getText');
            if(gdEcu!=null && gdEcu.datagrid('getSelected')!=null){
                row['customEcu']={};
                row['customEcu']['id'] = gdEcu.datagrid('getSelected')['id'];
                row['customEcu']['showNameEn'] = gdEcu.datagrid('getSelected')['showNameEn'];
                row['customEcu']['showNameCn'] = gdEcu.datagrid('getSelected')['showNameCn'];
            }
            $.post('/iov/metadata/vehDtc/save',
                {
                 'id':row['id'],
                 'ecuId':row['customEcu']['id'],
                 'code':row['code'],
                 'level':row['level'],
                 'failureType':row['failureType'],
                 'dispFlag':row['dispFlag'],
                 'describCh':row['describCh'],
                 'describEn':row['describEn'],
                 'overruledFlag':row['overruledFlag']
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
vehDtc.endEditing=function(){
    var gd=$('#dtcList');
    if (vehDtc.editIndex == undefined){
        return true
    }
    if (gd.datagrid('validateRow', vehDtc.editIndex)){
        gd.datagrid('endEdit', vehDtc.editIndex);
        vehDtc.editIndex = undefined;
        return true;
    }
    return false;
};