var vehOnline={
	curVehModeCode:'all',
    curVgIp:'all',
    curVgPort:'all',
    contextRoot:'/',
    width:800,
    height:600
};
//初始化
vehOnline.init=function(ctxRoot,width,height){
	this.contextRoot = ctxRoot;
    this.width = width;
    this.height = height;
	this.initVgTree();
	this.initOnlineListGrid();
    this.initDialog();
};
vehOnline.reloadVgTree=function(){
    $('#vgTree').treegrid('reload');
};
//列表树
vehOnline.initVgTree=function(){
	$('#vgTree').treegrid({
		url:'/iov/monitor/vehOnline/showVgTree',
		loadMsg:'数据加载中...',
		iconCls:'icon-save',
		width:360,
		height:vehOnline.height,
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
        showFooter:true,
		columns:[[
            {field:'text',sortable:false,align:'left'}
		]],
		onClickRow:function(row){
            var gd=$('#onlineList');
            if(row['vehModeCode']!='all'){
                vehOnline.curVehModeCode=row['vehModeCode'];
                vehOnline.curVgIp=row['ip'];
                vehOnline.curVgPort=row['port'];
                $('#noSelectNode').attr("style","display:none");
                $('#nodeInfo').attr("style","display:block");
                gd.datagrid('getPanel').panel('setTitle',row['panelText']);
                gd.datagrid('load',{'vehModeCode':vehOnline.curVehModeCode,'ip':vehOnline.curVgIp,'port':vehOnline.curVgPort});
            }else{
                $('#noSelectNode').attr("style","display:block");
                $('#nodeInfo').attr("style","display:none");
            }
		},
		toolbar:[{
			text:'刷新',
			iconCls:'icon-reload',
			handler:function(){
                vehOnline.reloadVgTree();
			}
		},{
            text:'搜索',
            iconCls:'icon-search',
            handler:function(){
                $('#dd').dialog('open');
            }
        }]
	});
};
//在线车辆列表
vehOnline.initOnlineListGrid=function(){
	$('#onlineList').datagrid({
		title:"连接车辆",
		url:'/iov/monitor/vehOnline/list',
		loadMsg:'数据加载中...',
		width:vehOnline.width-430,
		height:vehOnline.height-18,
		nowrap: false,
		striped: true,
		collapsible:false,
        fitColumns:false,
        singleSelect: true,
		sortName: 'status',
		sortOrder: 'asc',
		remoteSort: true,
		pageSize:20,
		idField:'id',
        pagination:true,
        rownumbers:true,
		columns:[[
            {field:'vin',title:'VIN',sortable:true,width:160,halign:'center'},
            {field:'status',title:'状态',sortable:true,width:60,halign:'center',formatter:function(value,row,index){
                if(value==0){
                    return "<span style='color:green;'>在线</span>";
                }else if(value==1){
                    return "<span style='color:red;'>下线</span>";
                }
            }},
            {field:'onlineTime',title:'最近上线时间',sortable:true,width:160,halign:'center'},
            {field:'offlineTime',title:'最近下线时间',sortable:true,width:160,halign:'center'},
            {field:'offlineReason',title:'下线原因',sortable:false,width:80,halign:'center',formatter:function(value,row,index){
                if(value==0){
                    return "正常";
                }else if(value==1){
                    return "超时";
                }
            }}
        ]]
	});
};
vehOnline.initDialog=function(){
    $('#dd').dialog({
        title: '车辆在线搜索',
        width: 380,
        height: 240,
        closed: true,
        cache: false,
        modal: true,
        buttons:[{
            text:'确定',
            iconCls: 'icon-search',
            handler:function(){
                $.messager.progress();
                $.post("/iov/monitor/vehOnline/search",{'vin':$('#vin').val()},function(respData){
                    var recs=respData['rows'];
                    var result="无数据";
                    if(recs.length>0){
                        result=recs[0]['vin']
                            +"<br/>当前状态:"+(recs[0]['status']==0 ? "<span style='color:green;'>在线</span>" : "<span style='color:red;'>离线</span>")
                            +"<br/>最近连接:"+recs[0]['vgIp']+":"+recs[0]['vgPort']
                            +"<br/>最近上线:"+recs[0]['onlineTime']
                            +"<br/>最近下线:"+recs[0]['offlineTime']
                            +"<br/>下线原因:"+(recs[0]['offlineReason']==0 ? "正常" : "超时");
                    }
                    $('#queryResult').html(result);
                    $.messager.progress('close');
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
};

