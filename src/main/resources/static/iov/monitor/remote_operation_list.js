var remoteOptList={
    contextRoot:"/",
    width:600,
    height:400
};
//初始化
remoteOptList.init=function(ctxRoot,gridWidth,gridHeight){
    this.contextRoot = ctxRoot;
    this.width = gridWidth;
    this.height = gridHeight;
    this.initList();
    this.initDetailWin();
};
//列表
remoteOptList.initList=function(){
    $('#remoteOptList').datagrid({
        url:'/iov/monitor/remoteOperation/list',
        loadMsg:'数据加载中...',
        emptyMsg:'无数据',
        width : remoteOptList.width,
        height : remoteOptList.height,
        nowrap : false,
        striped : true,
        collapsible:false,
        fitColumns:false,
        sortName : 'buildTime',
        sortOrder : 'desc',
        remoteSort : true,
        pageSize : 20,
        idField : 'id',
        pagination:true,
        rownumbers:true,
        singleSelect:true,
        columns:[[
            {field:'vin',title:'VIN',sortable:true,halign:'center'},
            {field:'operType',title:'指令',sortable:false,halign:'center',formatter:function(value,row,index){
                return row['operationTypeDefinition']['comment'];
            }},
            {field:'paramObj',title:'指令参数',sortable:false,halign:'center',formatter:function(value,row,index){
                if(value.length > 0){
                    return "<a href=\"javascript:remoteOptList.showDetail('指令参数','"+value+"');\">显示</a>";
                }else{
                    return "无";
                }
            }},
            {field:'buildTime',title:'创建时间',sortable:false,halign:'center'},
            {field:'statusUpdateTime',title:'完成时间',sortable:false,halign:'center'},
            {field:'status',title:'执行状态',sortable:false,halign:'center'},
            {field:'errCode',title:'错误代码',sortable:false,halign:'center'},
            {field:'ring',title:'唤醒记录',sortable:false,halign:'center',formatter:function(value,row,index){
                if(value=="1"){
                    return "<a href=\"javascript:remoteOptList.showRingRecs('"+row['cmdId']+"');\">显示</a>";
                }else{
                    return "无";
                }
            }},
            {field:'responseContent',title:'反馈信息',sortable:false,halign:'center',formatter:function(value,row,index){
                if(value.length > 0){
                    return "<a href=\"javascript:remoteOptList.showDetail('反馈信息','"+value+"');\">显示</a>";
                }else{
                    return "无";
                }
            }}
        ]]
    });
    $('#ringRecsList').datagrid({
        url:'/iov/monitor/remoteOperation/listRingRecs',
        loadMsg:'数据加载中...',
        emptyMsg:'无数据',
        width : 780,
        height : 522,
        nowrap : false,
        striped : true,
        collapsible:false,
        fitColumns:false,
        sortName : 'buildTime',
        sortOrder : 'desc',
        remoteSort : true,
        pageSize : 20,
        idField : 'id',
        pagination:true,
        rownumbers:true,
        singleSelect:true,
        columns:[[
            {field:'vin',title:'VIN',sortable:false,halign:'center'},
            {field:'dialerType',title:'类型',sortable:false,halign:'center'},
            {field:'phoneNum',title:'振铃号码',sortable:false,halign:'center'},
            {field:'buildTime',title:'创建时间',sortable:true,halign:'center'},
            {field:'finishTime',title:'完成时间',sortable:false,halign:'center'},
            {field:'status',title:'执行状态',sortable:false,halign:'center'},
            {field:'errorCode',title:'错误代码',sortable:false,halign:'center'}
        ]]
    });
};
//窗口
remoteOptList.initDetailWin=function(){
    $('#dd').dialog({
        title: '远控命令',
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
    $('#ringRecsWin').dialog({
        title: 'TBox唤醒记录',
        width: 800,
        height: 600,
        closed: true,
        cache: false,
        modal: true,
        buttons:[{
            text:'关闭',
            iconCls: 'icon-no',
            handler:function(){
                $('#ringRecsWin').dialog('close');
            }
        }]
    });
};
//展示
remoteOptList.showDetail=function(title,content){
    var dd=$('#dd');
    var ff=$('#ff');
    dd.dialog('setTitle',title);
    ff.form('load',{
        cmdDetail:$.base64.decode(content)
    });
    dd.dialog('open');
};
//振铃记录
remoteOptList.showRingRecs=function(cmdId){
    $('#ringRecsWin').dialog('open');
    $('#ringRecsList').datagrid('load',{'cmdId':cmdId});
};

//查询
remoteOptList.query=function(){
    $("#remoteOptList").datagrid("clearSelections");
    var vin=$("#queryForm input[name='vin']").val();
    var sTime=$("#buildStartTime").datetimebox('getValue');
    var eTime=$("#buildEndTime").datetimebox('getValue');
    $("#remoteOptList").datagrid('load',{'vin':vin,'buildStartTime':sTime,'buildEndTime':eTime});
};
//重置
remoteOptList.resetQuery=function(){
    $("#queryForm input[name='vin']").val("");
    $("#buildStartTime").datetimebox('setValue','');
    $("#buildEndTime").datetimebox('setValue','');
    remoteOptList.query();
};


