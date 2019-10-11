var vehStatusSnapshotList={
    contextRoot:"/",
    width:600,
    height:400
};
//初始化
vehStatusSnapshotList.init=function(ctxRoot,gridWidth,gridHeight){
    this.contextRoot = ctxRoot;
    this.width = gridWidth;
    this.height = gridHeight;
    this.initList();
    this.initDetailWin();
};
//列表
vehStatusSnapshotList.initList=function(){
    $('#vehStatusSnapshotList').datagrid({
        url:'/iov/monitor/vehStatusSnapshot/list',
        loadMsg:'数据加载中...',
        emptyMsg:'无数据',
        width : vehStatusSnapshotList.width,
        height : vehStatusSnapshotList.height,
        nowrap : false,
        striped : true,
        collapsible:false,
        fitColumns:false,
        sortName : 'vin',
        sortOrder : 'asc',
        remoteSort : true,
        pageSize : 20,
        idField : 'id',
        pagination:true,
        rownumbers:true,
        singleSelect:true,
        columns:[[
            {field:'vin',title:'VIN',sortable:true,halign:'center'},
            {field:'iccid',title:'ICCID',sortable:false,halign:'center'},
            {field:'imei',title:'IMEI',sortable:false,halign:'center'},
            {field:'veh_series',title:'车型',sortable:false,halign:'center'},
            {field:'update_ts',title:'最近更新',sortable:true,halign:'center'},
            {field:'isThresholdAlarm',title:'阀值告警',sortable:false,halign:'center'},
            {field:'isDiagnosisAlarm',title:'故障告警',sortable:false,halign:'center'},
            {field:'isOnline',title:'在线',sortable:false,halign:'center'},
            {field:'isDtcAlarm',title:'有DTC',sortable:false,halign:'center'},
            {field:'DTC',title:'DTC详情',sortable:false,halign:'center',formatter:function(value,row,index){
                if(value.length > 0){
                    return "<a href=\"javascript:vehStatusSnapshotList.showDetail('DTC','"+value+"');\">显示</a>";
                }else{
                    return "无";
                }
            }},
            {field:'DRIVE',title:'驾驶数据',sortable:false,halign:'center',formatter:function(value,row,index){
                if(value.length > 0){
                    return "<a href=\"javascript:vehStatusSnapshotList.showDetail('驾驶行为','"+value+"');\">显示</a>";
                }else{
                    return "无";
                }
            }},
            {field:'status_snapshot',title:'车况',sortable:false,halign:'center',formatter:function(value,row,index){
                if(value.length > 0){
                    return "<a href=\"javascript:vehStatusSnapshotList.showDetail('车况快照','"+value+"');\">显示</a>";
                }else{
                    return "无";
                }
            }}
        ]]
    });
};
//窗口
vehStatusSnapshotList.initDetailWin=function(){
    $('#dd').dialog({
        title: '详情',
        width: 600,
        height: 420,
        closed: true,
        cache: false,
        modal: true,
        buttons:[{
            text:'关闭',
            iconCls: 'icon-no',
            handler:function(){
                $('#dd').dialog('close');
            }
        }]
    });
};
//展示
vehStatusSnapshotList.showDetail=function(title,content){
    var dd=$('#dd');
    var ff=$('#ff');
    dd.dialog('setTitle',title);
    ff.form('load',{
        detail:$.base64.decode(content)
    });
    dd.dialog('open');
};
//查询
vehStatusSnapshotList.query=function(){
    $("#vehStatusSnapshotList").datagrid("clearSelections");
    var vin=$("#queryForm input[name='vin']").val();
    $("#vehStatusSnapshotList").datagrid('load',{'vin':vin});
};
//重置
vehStatusSnapshotList.resetQuery=function(){
    $("#queryForm input[name='vin']").val("");
    vehStatusSnapshotList.query();
};


