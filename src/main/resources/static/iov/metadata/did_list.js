/**
 * Created by dyl on 12/7/16.
 */
var didList={
    contextRoot:"/",
    width:600,
    height:400
};
//初始化
didList.init=function(ctxRoot,gridWidth,gridHeight){
    this.contextRoot = ctxRoot;
    this.width = gridWidth;
    this.height = gridHeight;
    this.initList();
    this.initDetailWin();
    this.initForm();
};
//列表
didList.initList=function(){
    $('#didList').datagrid({
        url:'/iov/metadata/did/list',
        loadMsg:'数据加载中...',
        emptyMsg:'无数据',
        width : didList.width,
        height : didList.height,
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
            {field:'showNameEn',title:'英文名',sortable:true,width:120,halign:'center'},
            {field:'showNameCn',title:'中文名',sortable:true,width:120,halign:'center'},
            {field:'remark',title:'备注',sortable:true,width:120,halign:'center'}
        ]],
        onDblClickRow:function(rowIndex, row){
            didList.showDetail(row.id);
        },
        toolbar:[{
            text:'增加',
            iconCls:'icon-add',
            handler:function(){
                didList.showDetail();
            }
        },{
            text:'编辑',
            iconCls:'icon-edit',
            handler:function(){
                var rec=$("#didList").datagrid("getSelections");
                if(rec.length==0){
                    $.messager.alert('提示','请选择记录','info');
                }else{
                    didList.showDetail(rec[0].id);
                }
            }
        },{
            text:'删除',
            iconCls:'icon-remove',
            handler:function(){
                var rec=$("#didList").datagrid("getSelections");
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
                        $.post("/iov/metadata/did/remove",{"ids":ids},function(data){
                            var gd=$("#didList");
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
didList.initDetailWin=function(){
    $('#detailWin').window({
        width:600,
        height:450,
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
didList.initForm=function(){
    //验证
    $("#ff input[name='code']").validatebox({
        required: true,
        validType: 'length[1,30]',
        missingMessage:'必填',
        invalidMessage:'30字内'
    });
    $("#ff input[name='showNameEn']").validatebox({
        required: true,
        validType: 'length[1,30]',
        missingMessage:'必填',
        invalidMessage:'30字内'
    });
};

//保存
didList.save=function(){
    $('#ff').form('submit',{
        url:'/iov/metadata/did/save',
        onSubmit: function(){
            var ok="false";
            if($('#ff').form('validate')){
                ok = $.ajax({
                    type: "POST",
                    url: "/iov/metadata/did/check",
                    data:{"id":$("#ff input[name='id']").val(),"code":$("#ff input[name='code']").val()},
                    async: false
                }).responseText;
            }
            if("true"==ok){
                $.messager.progress();
                return true;
            }else{
                $.messager.alert('错误','该信号已存在!','error');
                return false;
            }
        },
        success:function(responseText){
            $.messager.progress('close');
            $('#detailWin').window('close');
            $('#didList').datagrid('reload');
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
didList.showDetail=function(id){
    $('#ff').form('clear');
    if(id){
        $.post("/iov/metadata/did/get",{"id":id},function(data){
            $('#ff').form('load',data);
        });
    }
    $('#detailWin').window('open');
};
//查询
didList.query=function(){
    $("#didList").datagrid("clearSelections");
    $("#didList").datagrid('load',{
        'showNameEn':$("#queryForm input[name='showNameEn']").val(),
        'showNameCn':$("#queryForm input[name='showNameCn']").val(),
        'code':$("#queryForm input[name='code']").val()});
};
//重置
didList.resetQuery=function(){
    $("#queryForm input[name='code']").val("");
    $("#queryForm input[name='showNameEn']").val("");
    $("#queryForm input[name='showNameCn']").val("");
    didList.query();
};


