<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
%>
<!DOCTYPE html>
<html>
<head>
	<base href="<%=basePath%>">
<meta charset="UTF-8">

<link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet" />
<script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
<script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>
<link rel="stylesheet" type="text/css" href="jquery/bootstrap-datetimepicker-master/css/bootstrap-datetimepicker.min.css">
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/js/bootstrap-datetimepicker.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/locale/bootstrap-datetimepicker.zh-CN.js"></script>
<script type="text/javascript">



	//默认情况下取消和保存按钮是隐藏的
	var cancelAndSaveBtnDefault = true;
	
	$(function(){
        $(".time").datetimepicker({
            minView: "month",
            language:  'zh-CN',
            format: 'yyyy-mm-dd',
            autoclose: true,
            todayBtn: true,
            pickerPosition: "bottom-left",
            readOnly : true
        });
		$("#remark").focus(function(){
			if(cancelAndSaveBtnDefault){
				//设置remarkDiv的高度为130px
				$("#remarkDiv").css("height","130px");
				//显示
				$("#cancelAndSaveBtn").show("2000");
				cancelAndSaveBtnDefault = false;
			}
		});
		
		$("#cancelBtn").click(function(){
			//显示
			$("#cancelAndSaveBtn").hide();
			//设置remarkDiv的高度为130px
			$("#remarkDiv").css("height","90px");
			cancelAndSaveBtnDefault = true;
		});
		
		$(".remarkDiv").mouseover(function(){
			$(this).children("div").children("div").show();
		});
		
		$(".remarkDiv").mouseout(function(){
			$(this).children("div").children("div").hide();
		});
		
		$(".myHref").mouseover(function(){
			$(this).children("span").css("color","red");
		});
		
		$(".myHref").mouseout(function(){
			$(this).children("span").css("color","#E6E6E6");
		});
		//页面加载完毕后，展现该市场活动关联的备注信息列表
		showRemarkList();

        $("#remarkBody").on("mouseover",".remarkDiv",function(){
            $(this).children("div").children("div").show();
        })
        $("#remarkBody").on("mouseout",".remarkDiv",function(){
            $(this).children("div").children("div").hide();
        })

        $("#remarkSaveBtn").click(function () {
            if($("#remark").val() != ""){
                $.ajax({
                    url : "workbench/activity/saveRemark.do",
                    data : {
                        "noteContent" : $("#remark").val(),
                        "activityId" : "${requestScope.activity.id}"
                    },
                    type : "POST",
                    dataType : "json",
                    success : function (data) {
                        if(data.success){
                            //添加成功后
                            showRemarkList()
                            $("#remark").val("")
                        }else{
                            alert("添加备注失败")
                        }
                    }

                })
            }else{
                $("#remarkSpan").html("备注信息不能为空")
            }
        })

        $("#remark").focus(function () {
            $("#remarkSpan").html("")
        })

        $("#updateRemarkBtn").click(function () {
            if($("#noteContent").val() != ""){
                $.ajax({
                    url : "workbench/activity/updateRemark.do",
                    data : {
                        "id" : $("#remarkId").val(),
                        "noteContent" : $("#noteContent").val()
                    },
                    type : "POST",
                    dataType : "json",
                    success : function (data) {
                        if(data.success){
                            showRemarkList()
                            $("#editRemarkModal").modal("hide")
                        }else{
                            alert("修改备注信息失败")
                        }
                    }

                })
            }else{
                $("#editRemarkDiv").html("<span style='font-size: 20px'>备注信息不能为空</span>")
            }

        })

        $("#noteContent").focus(function () {
            $("#editRemarkDiv").html("")
        })

        $("#noteContent").blur(function () {
            if($("#noteContent").val() == ""){
                $("#editRemarkDiv").html("<span style='font-size: 20px'>备注信息不能为空</span>")
            }
        })

        $("#editActivityBtn").click(function () {
            $.ajax({
                url : "workbench/activity/getUserListAndActivity.do",
                data : {
                    "id" : $("#activityId").val()
                },
                type : "get",
                dataType : "json",
                success : function (data) {
                    /*
                        data
                            用户列表
                            市场活动对象
                            {"uList":[{用户1},{2},{3}],"activity":{市场活动}}
                     */
                    //处理所有者的下拉框
                    let html = "<option></option>";
                    $.each(data.userList,function(index,element){
                        html += "<option value='" + element.id + "'>" + element.name + "</option>";
                    })

                    $("#edit-owner").html(html);

                    //处理单条activity
                    $("#edit-id").val(data.activity.id)
                    $("#edit-name").val(data.activity.name)
                    $("#edit-owner").val(data.activity.owner)
                    $("#edit-startDate").val(data.activity.startDate)
                    $("#edit-endDate").val(data.activity.endDate)
                    console.log("处理成功！")
                    $("#edit-cost").val(data.activity.cost)
                    $("#edit-description").val(data.activity.description)

                    //所有值都填写好之后，打开修改操作的模态窗口
                    $("#editActivityModal").modal("show");
                }
            })
        })

        $("#updateBtn").click(function () {
            $.ajax({
                url : "workbench/activity/update.do",
                type : "POST",
                data : {
                    "id" : $("#activityId").val(),
                    "owner" : $.trim($("#edit-owner").val()),
                    "name" : $.trim($("#edit-name").val()),
                    "startDate" : $.trim($("#edit-startDate").val()),
                    "endDate" : $.trim($("#edit-endDate").val()),
                    "cost" : $.trim($("#edit-cost").val()),
                    "description" : $.trim($("#edit-description").val())
                },
                dataType : "json",
                success : function (data) {
                    /*
                        data
                            {"success":true/false}
                    */
                    if(data.success){
                        //修改成功后
                        //刷新市场活动信息列表（局部刷新）
                        // pageList(1,10)
                        /*
                            修改操作后，应该维持在当前页，维持每页展现的记录数
                         */
                        // pageList($("#activityPage").bs_pagination('getOption', 'currentPage')
                        //     ,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));
                        location.reload(true)
                        //清空添加操作模态窗口中的数据
                        //这个方法用不了
                        // $("#activityAddForm").reset()
                        $("#activityAddForm")[0].reset()
                        //关闭修改操作的模态窗口
                        $("#editActivityModal").modal("hide")
                    }else{
                        alert("修改市场活动失败")
                    }
                }
            })
        })

        $("#deleteActivityBtn").click(function () {
            if(confirm("确定删除该记录？")){

                $.ajax({
                    url : "workbench/activity/delete.do",
                    data : {
                        "id" : $("#activityId").val()
                    },
                    type : "POST",
                    dataType : "json",
                    success : function (data) {
                        if(data.success){
                            //删除成功后
                            //返回上一级并刷新
                            // location.replace(this.href);event.returnValue=false;
                            // document.parentWindow.location.reload(true)
                            // window.history.back()
                            window.location.href=document.referrer;
                        }else{
                            alert('删除市场活动失败')
                        }
                    }

                })
            }

        })

    });

	function showRemarkList() {
		$.ajax({
			url : "workbench/activity/getRemarkListByAid.do",
			data : {
				"activityId" : "${requestScope.activity.id}"
			},
			type : "get",
			dataType : "json",
			success : function (data) {
				var html = "";
				$.each(data,function (index,element) {
					html += '<div class="remarkDiv" style="height: 60px;">'
					html += '<img title="${requestScope.activity.name}" src="image/user-thumbnail.png" style="width: 30px; height:30px;">'
					html += '<div style="position: relative; top: -40px; left: 40px;" >'
					html += '<h5>' + element.noteContent + '</h5>'
					html += '<font color="gray">市场活动</font> <font color="gray">-</font> <b>${requestScope.activity.name}</b> <small style="color: gray;"> ' + (element.editFlag == 0 ? element.createTime : element.editTime) + ' 由' + (element.editFlag == 0 ? element.createBy : element.editBy) + (element.editFlag == 0 ? "添加" : "修改") +'</small>'
					html += '<div style="position: relative; left: 500px; top: -30px; height: 30px; width: 100px; display: none;">'
					html += '<a class="myHref" href="javascript:void(0);" onclick="editRemark(\'' + element.id + '\')"><span class="glyphicon glyphicon-edit" style="font-size: 20px; color: #FF0000;"></span></a>'
					html += '&nbsp;&nbsp;&nbsp;&nbsp;'
					html += '<a class="myHref" href="javascript:void(0);" onclick="deleteRemark(\'' + element.id + '\')"><span class="glyphicon glyphicon-remove" style="font-size: 20px; color: #FF0000;"></span></a>'
					html += '</div>'
					html += '</div>'
					html += '</div>'
				})

				$("#remarkActivityDiv").html(html)
                if($("#remarkActivityDiv").html() == ""){
                    $("#remarkActivityDiv").html("<b><i>暂时没有备注，请添加！</i></b>")
                }
			}

		})
	}

	function editRemark(id) {
        $("#editRemarkModal").modal("show")
        $.ajax({
            url : "workbench/activity/getRemarkById.do",
            data : {
                "id" : id
            },
            type : "get",
            dataType : "json",
            success : function (data) {
                $("#remarkId").val(data.id)
                $("#noteContent").val(data.noteContent)
            }

        })
    }

	function deleteRemark(id) {
	    if(confirm("确认删除该备注?")){
            $.ajax({
                url : "workbench/activity/deleteRemark.do",
                data : {
                    "id" : id
                },
                type : "POST",
                dataType : "json",
                success : function (data) {
                    if(data.success){
                        //删除备注成功
                        showRemarkList()
                    }else{
                        alert("删除备注失败")
                    }
                }

            })
        }
    }

</script>

</head>
<body>
	
	<!-- 修改市场活动备注的模态窗口 -->
	<div class="modal fade" id="editRemarkModal" role="dialog">
		<%-- 备注的id --%>
		<input type="hidden" id="remarkId">
        <div class="modal-dialog" role="document" style="width: 40%;">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">
                        <span aria-hidden="true">×</span>
                    </button>
                    <h4 class="modal-title" id="myModalLabel">修改备注</h4>
                </div>
                <div class="modal-body">
                    <form class="form-horizontal" role="form">
                        <div class="form-group">
                            <label for="edit-describe" class="col-sm-2 control-label">内容</label>
                            <div class="col-sm-10" style="width: 81%;">
                                <textarea class="form-control" rows="3" id="noteContent"></textarea>
                            </div>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <div style="color: red" id="editRemarkDiv"></div>
                    <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                    <button type="button" class="btn btn-primary" id="updateRemarkBtn">更新</button>
                </div>
            </div>
        </div>
    </div>

    <!-- 修改市场活动的模态窗口 -->
    <div class="modal fade" id="editActivityModal" role="dialog">
        <div class="modal-dialog" role="document" style="width: 85%;">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">
                        <span aria-hidden="true">×</span>
                    </button>
                    <h4 class="modal-title" id="myModalLabel">修改市场活动</h4>
                </div>
                <div class="modal-body">

                    <form class="form-horizontal" role="form">

                        <div class="form-group">
                            <label for="edit-marketActivityOwner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <select class="form-control" id="edit-owner">

                                </select>
                            </div>
                            <label for="edit-marketActivityName" class="col-sm-2 control-label">名称<span style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="edit-name" >
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="edit-startTime" class="col-sm-2 control-label">开始日期</label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control time" id="edit-startDate">
                            </div>
                            <label for="edit-endTime" class="col-sm-2 control-label">结束日期</label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control time" id="edit-endDate">
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="edit-cost" class="col-sm-2 control-label">成本</label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="edit-cost">
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="edit-describe" class="col-sm-2 control-label">描述</label>
                            <div class="col-sm-10" style="width: 81%;">
                                <textarea class="form-control" rows="3" id="edit-description"></textarea>
                            </div>
                        </div>

                    </form>

                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                    <button type="button" class="btn btn-primary" id="updateBtn">更新</button>
                </div>
            </div>
        </div>
    </div>

	<!-- 返回按钮 -->
	<div style="position: relative; top: 35px; left: 10px;">
		<a href="javascript:void(0);" onclick="window.history.back();"><span class="glyphicon glyphicon-arrow-left" style="font-size: 20px; color: #DDDDDD"></span></a>
	</div>
	
	<!-- 大标题 -->
	<div style="position: relative; left: 40px; top: -30px;">
		<div class="page-header">
            <input type="hidden" id="activityId" value="${requestScope.activity.id}">
			<h3>市场活动-${requestScope.activity.name} <small>${requestScope.activity.startDate} ~ ${requestScope.activity.endDate}</small></h3>
		</div>
		<div style="position: relative; height: 50px; width: 250px;  top: -72px; left: 700px;">
			<button type="button" class="btn btn-default" id="editActivityBtn"><span class="glyphicon glyphicon-edit"></span> 编辑</button>
			<button type="button" class="btn btn-danger" id="deleteActivityBtn"><span class="glyphicon glyphicon-minus"></span> 删除</button>
		</div>
	</div>
	
	<!-- 详细信息 -->
	<div style="position: relative; top: -70px;">
		<div style="position: relative; left: 40px; height: 30px;">
			<div style="width: 300px; color: gray;">所有者</div>
			<div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${requestScope.activity.owner}</b></div>
			<div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">名称</div>
			<div style="width: 300px;position: relative; left: 650px; top: -60px;"><b>${requestScope.activity.name}</b></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
		</div>

		<div style="position: relative; left: 40px; height: 30px; top: 10px;">
			<div style="width: 300px; color: gray;">开始日期</div>
			<div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${requestScope.activity.startDate}</b></div>
			<div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">结束日期</div>
			<div style="width: 300px;position: relative; left: 650px; top: -60px;"><b>${requestScope.activity.endDate}</b></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 20px;">
			<div style="width: 300px; color: gray;">成本</div>
			<div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${requestScope.activity.cost}</b></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -20px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 30px;">
			<div style="width: 300px; color: gray;">创建者</div>
			<div style="width: 500px;position: relative; left: 200px; top: -20px;"><b>${requestScope.activity.createBy}&nbsp;&nbsp;</b><small style="font-size: 10px; color: gray;">${requestScope.activity.createTime}</small></div>
			<div style="height: 1px; width: 550px; background: #D5D5D5; position: relative; top: -20px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 40px;">
			<div style="width: 300px; color: gray;">修改者</div>
			<div style="width: 500px;position: relative; left: 200px; top: -20px;"><b>${requestScope.activity.editBy}&nbsp;&nbsp;</b><small style="font-size: 10px; color: gray;">${requestScope.activity.editTime}</small></div>
			<div style="height: 1px; width: 550px; background: #D5D5D5; position: relative; top: -20px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 50px;">
			<div style="width: 300px; color: gray;">描述</div>
			<div style="width: 630px;position: relative; left: 200px; top: -20px;">
				<b>
					${requestScope.activity.description}
				</b>
			</div>
			<div style="height: 1px; width: 850px; background: #D5D5D5; position: relative; top: -20px;"></div>
		</div>
	</div>
	
	<!-- 备注 -->
	<div id="remarkBody" style="position: relative; top: 30px; left: 40px;">
		<div class="page-header">
			<h4>备注</h4>
		</div>

		<div id="remarkActivityDiv">

		</div>
		<!-- 备注1 -->
		<%--<div class="remarkDiv" style="height: 60px;">
			<img title="zhangsan" src="image/user-thumbnail.png" style="width: 30px; height:30px;">
			<div style="position: relative; top: -40px; left: 40px;" >
				<h5>哎呦！</h5>
				<font color="gray">市场活动</font> <font color="gray">-</font> <b>发传单</b> <small style="color: gray;"> 2017-01-22 10:10:10 由zhangsan</small>
				<div style="position: relative; left: 500px; top: -30px; height: 30px; width: 100px; display: none;">
					<a class="myHref" href="javascript:void(0);"><span class="glyphicon glyphicon-edit" style="font-size: 20px; color: #E6E6E6;"></span></a>
					&nbsp;&nbsp;&nbsp;&nbsp;
					<a class="myHref" href="javascript:void(0);"><span class="glyphicon glyphicon-remove" style="font-size: 20px; color: #E6E6E6;"></span></a>
				</div>
			</div>
		</div>--%>
		
		<!-- 备注2 -->
		<%--<div class="remarkDiv" style="height: 60px;">
			<img title="zhangsan" src="image/user-thumbnail.png" style="width: 30px; height:30px;">
			<div style="position: relative; top: -40px; left: 40px;" >
				<h5>呵呵！</h5>
				<font color="gray">市场活动</font> <font color="gray">-</font> <b>发传单</b> <small style="color: gray;"> 2017-01-22 10:20:10 由zhangsan</small>
				<div style="position: relative; left: 500px; top: -30px; height: 30px; width: 100px; display: none;">
					<a class="myHref" href="javascript:void(0);"><span class="glyphicon glyphicon-edit" style="font-size: 20px; color: #E6E6E6;"></span></a>
					&nbsp;&nbsp;&nbsp;&nbsp;
					<a class="myHref" href="javascript:void(0);"><span class="glyphicon glyphicon-remove" style="font-size: 20px; color: #E6E6E6;"></span></a>
				</div>
			</div>
		</div>--%>
		
		<div id="remarkDiv" style="background-color: #E6E6E6; width: 870px; height: 90px">
			<form role="form" style="position: relative;top: 10px; left: 10px;">
				<textarea id="remark" class="form-control" style="width: 850px; resize : none;" rows="2"  placeholder="添加备注..."></textarea>
				<p id="cancelAndSaveBtn" style="position: relative;left: 737px; top: 10px; display: none;">
					<button id="cancelBtn" type="button" class="btn btn-default">取消</button>
					<button type="button" class="btn btn-primary" id="remarkSaveBtn">保存</button>
				</p>
                <span style="color: red;" id="remarkSpan"></span>
			</form>
		</div>
	</div>
	<div style="height: 200px;"></div>
</body>
</html>