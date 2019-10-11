var eventList={
    contextRoot:"/",
    width:600,
    height:400
};
//初始化
eventList.init=function(ctxRoot,gridWidth,gridHeight){
    this.contextRoot = ctxRoot;
    this.width = gridWidth;
    this.height = gridHeight;
    this.initList();
    this.initDetailWin();
    this.initForm();
};
//列表
eventList.initList=function(){
    $('#eventList').datagrid({
        url:'/iov/metadata/event/list',
        loadMsg:'数据加载中...',
        emptyMsg:'无数据',
        width : eventList.width,
        height : eventList.height,
        nowrap : false,
        striped : true,
        collapsible:false,
        fitColumns:true,
        sortName : 'code',
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
            {field:'code',title:'代码',sortable:true,width:120,halign:'center'},
            {field:'name',title:'名称',sortable:true,width:120,halign:'center'},
            {field:'grade',title:'等级',sortable:true,width:80,align:'center',formatter:function(value,row,index){
                if (value=='0'){
                    return '低';
                } else if (value=='1') {
                    return '中';
                } else if (value=='2') {
                    return '高';
                } else{
                    return '';
                }
            }},
            {field:'remark',title:'备注',sortable:true,width:120,halign:'center'}
        ]],
        onDblClickRow:function(rowIndex, row){
            eventList.showDetail(row.id);
        },
        toolbar:[{
            text:'增加',
            iconCls:'icon-add',
            handler:function(){
                eventList.showDetail();
            }
        },{
            text:'编辑',
            iconCls:'icon-edit',
            handler:function(){
                var rec=$("#eventList").datagrid("getSelections");
                if(rec.length==0){
                    $.messager.alert('提示','请选择记录','info');
                }else{
                    eventList.showDetail(rec[0].id);
                }
            }
        },{
            text:'删除',
            iconCls:'icon-remove',
            handler:function(){
                var rec=$("#eventList").datagrid("getSelections");
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
                        $.post("/iov/metadata/event/remove",{"ids":ids},function(data){
                            var gd=$("#eventList");
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
eventList.initDetailWin=function(){
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
eventList.initForm=function(){
    //验证
    $("#ff input[name='code']").validatebox({
        required: true,
        validType: 'length[1,30]',
        missingMessage:'必填',
        invalidMessage:'30字内'
    });
    $("#ff input[name='name']").validatebox({
        required: true,
        validType: 'length[1,30]',
        missingMessage:'必填',
        invalidMessage:'30字内'
    });
};
//保存
eventList.save=function(){
    $('#ff').form('submit',{
        url:'/iov/metadata/event/save',
        onSubmit: function(){
            var ok="false";
            if($('#ff').form('validate')){
                ok = $.ajax({
                    type: "POST",
                    url: "/iov/metadata/event/check",
                    data:{"id":$("#ff input[name='id']").val(),"code":$("#ff input[name='code']").val()},
                    async: false
                }).responseText;
            }
            if("true"==ok){
                $.messager.progress();
                return true;
            }else{
                $.messager.alert('错误','事件代码已经存在!','error');
                return false;
            }
        },
        success:function(responseText){
            $.messager.progress('close');
            $('#detailWin').window('close');
            $('#eventList').datagrid('reload');
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
eventList.showDetail=function(id){
    $('#ff').form('clear');
    if(id){
        $.post("/iov/metadata/event/get",{"id":id},function(data){
            $('#ff').form('load',data);
        });
    }
    $('#detailWin').window('open');
};
//查询
eventList.query=function(){
    $("#eventList").datagrid("clearSelections");
    $("#eventList").datagrid('load',{'name':$("#queryForm input[name='name']").val(),'code':$("#queryForm input[name='code']").val()});
};
//重置
eventList.resetQuery=function(){
    $("#queryForm input[name='code']").val("");
    $("#queryForm input[name='name']").val("");
    eventList.query();
};


