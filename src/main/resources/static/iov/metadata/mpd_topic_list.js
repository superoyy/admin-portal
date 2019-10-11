var mpdTopicList={
    contextRoot:"/",
    width:600,
    height:400
};
//初始化
mpdTopicList.init=function(ctxRoot,gridWidth,gridHeight){
    this.contextRoot = ctxRoot;
    this.width = gridWidth;
    this.height = gridHeight;
    this.initList();
    this.initDetailWin();
    this.initForm();
};
//列表
mpdTopicList.initList=function(){
    $('#mpdTopicList').datagrid({
        url:'/iov/metadata/mpdTopic/list',
        loadMsg:'数据加载中...',
        emptyMsg:'无数据',
        width : mpdTopicList.width,
        height : mpdTopicList.height,
        nowrap : false,
        striped : true,
        collapsible:false,
        fitColumns:true,
        sortName : 'topicName',
        sortOrder : 'asc',
        remoteSort : true,
        pageSize : 20,
        idField : 'id',
        pagination:true,
        rownumbers:true,
        frozenColumns:[[
            {field:'id',checkbox:true}
        ]],
        columns:[[
            {field:'topicName',title:'名称',sortable:true,width:200,halign:'center'},
            {field:'eventList',title:'接收事件',sortable:false,width:400,halign:'center'},
            {field:'status',title:'状态',sortable:true,width:80,halign:'center'}
        ]],
        onDblClickRow:function(rowIndex, row){
            mpdTopicList.showDetail(row.id);
        },
        toolbar:[{
            text:'增加',
            iconCls:'icon-add',
            handler:function(){
                mpdTopicList.showDetail();
            }
        },{
            text:'编辑',
            iconCls:'icon-edit',
            handler:function(){
                var rec=$("#mpdTopicList").datagrid("getSelections");
                if(rec.length==0){
                    $.messager.alert('提示','请选择记录','info');
                }else{
                    mpdTopicList.showDetail(rec[0].id);
                }
            }
        },{
            text:'删除',
            iconCls:'icon-remove',
            handler:function(){
                var rec=$("#mpdTopicList").datagrid("getSelections");
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
                        $.post("/iov/metadata/mpdTopic/remove",{"ids":ids},function(data){
                            var gd=$("#mpdTopicList");
                            gd.datagrid("clearSelections");
                            gd.datagrid("reload");
                        });
                    }
                });
            }
        }]
    });
};
//表单窗口
mpdTopicList.initDetailWin=function(){
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
mpdTopicList.initForm=function(){
    //验证
    $("#ff input[name='topicName']").validatebox({
        required: true,
        validType: 'length[1,30]',
        missingMessage:'必填',
        invalidMessage:'30字内'
    });
    $("#ff input[name='status']").combobox({
        required: true,
        valueField: 'value',
        textField: 'label',
        editable:false,
        value:'1',
        data: [{
            label: '有效',
            value: '1'
        },{
            label: '无效',
            value: '0'
        }]
    });
};
//保存
mpdTopicList.save=function(){
    $('#ff').form('submit',{
        url:'/iov/metadata/mpdTopic/save',
        onSubmit: function(){
            var ok="false";
            if($('#ff').form('validate')){
                ok = $.ajax({
                    type: "POST",
                    url: "/iov/metadata/mpdTopic/check",
                    data:{"id":$("#ff input[name='id']").val(),"topicName":$("#ff input[name='topicName']").val()},
                    async: false
                }).responseText;
            }
            if("true"==ok){
                $.messager.progress();
                return true;
            }else{
                $.messager.alert('错误','队列名称已经存在!','error');
                return false;
            }
        },
        success:function(responseText){
            $.messager.progress('close');
            $('#detailWin').window('close');
            $('#mpdTopicList').datagrid('reload');
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
mpdTopicList.showDetail=function(id){
    $('#ff').form('clear');
    if(id){
        $.post("/iov/metadata/mpdTopic/get",{"id":id},function(data){
            $('#ff').form('load',data);
        });
    }
    $('#detailWin').window('open');
};
//查询
mpdTopicList.query=function(){
    $("#mpdTopicList").datagrid("clearSelections");
    $("#mpdTopicList").datagrid('load',{'topicName':$("#queryForm input[name='topicName']").val()});
};
//重置
mpdTopicList.resetQuery=function(){
    $("#queryForm input[name='topicName']").val("");
    mpdTopicList.query();
};


