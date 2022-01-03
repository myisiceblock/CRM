<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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


<link href="jquery/bootstrap-datetimepicker-master/css/bootstrap-datetimepicker.min.css" type="text/css" rel="stylesheet" />
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/js/bootstrap-datetimepicker.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/locale/bootstrap-datetimepicker.zh-CN.js"></script>

<script type="text/javascript">
	$(function(){
		$(".time").datetimepicker({
			minView: "month",
			language:  'zh-CN',
			format: 'yyyy-mm-dd',
			autoclose: true,
			todayBtn: true,
			pickerPosition: "top-left",
			readOnly : true
		});

		$("#isCreateTransaction").click(function(){
			if(this.checked){
				$("#create-transaction2").show(200);
			}else{
				$("#create-transaction2").hide(200);
			}
		});

		$("#searchActivitySource").click(function () {
			$.ajax({
				url : "workbench/clue/getActivityList.do",
				type : "get",
				dataType : "json",
				success : function (data) {
					var html = "";

					$.each(data,function (index,element) {
						html += '<tr>'
						html += '<td><input type="radio" name="activity" value="' + element.id + '"/></td>'
						html += '<td id="' + element.id + '">' + element.name + '</td>'
						html += '<td>' + element.startDate + '</td>'
						html += '<td>' + element.endDate + '</td>'
						html += '<td>' + element.owner + '</td>'
						html += '</tr>'
					})

					$("#searchActivityBody").html(html)
					$("#searchActivityModal").modal("show")
				}

			})
		})

		$("#searchActivityName").keydown(function (event) {
			if(event.keyCode==13){

				$.ajax({
					url : "workbench/clue/getActivityByName.do",
					data : {
						"activityName" : $.trim($("#searchActivityName").val())
					},
					type : "get",
					dataType : "json",
					success : function (data) {
						$("#searchActivityBody").html("")
						var html = ""

						$.each(data,function (index,element) {
							html += '<tr>'
							html += '<td><input type="radio" value="' + element.id + '" name="activity"/></td>'
							html += '<td id="' + element.id + '">' + element.name +'</td>'
							html += '<td>' + element.startDate + '</td>'
							html += '<td>' + element.endDate + '</td>'
							html += '<td>' + element.owner+ '</td>'
							html += '</tr>'
						})

						$("#searchActivityBody").html(html)
					}

				})
				return false
			}
		})

		$("#submitActivityBtn").click(function () {
			var $xz = $("input[name=activity]:checked")
			var activityId = $xz.val()
			var name = $("#"+activityId).html()
			$("#activityId").val(activityId)
			$("#activityName").val(name)
			$("#searchActivityModal").modal("hide")
		})

		$("#convertBtn").click(function () {
			/*
				提交请求到后台，执行线索转换的操作，应该发出传统请求
				请求结束后，最终响应回到线索列表页

				根据“为客户创建交易”的复选框有没有挑√来判断是否需要创建交易

			 */
			if($("#isCreateTransaction").prop("checked")){
				// alert("需要创建交易")
				//如果需要创建交易，除了要为后台传递clueId之外，还得为后台传递交易表单中的信息，金额，预计成交日期，交易名称，阶段，市场活动源(id)
				<%--window.location.href="workbench/clue/convert.do?clueId=${requestScope.clue.id}&money=xxx&expectedDate=xxx&name=xxx&stage=xxx&activityId=xxx";--%>
				//以上传递参数的方式很麻烦，而且表单一旦扩充，挂载的参数有可能超出浏览器地址栏的上限
				//我们想到使用提交交易表单的形式来发出本次的传统请求
				//提交表单的参数不用我们手动去挂载（表单中写name属性），提交表单能够提交post请求

				//提交表单
				$("#tramForm").submit()
			}else{
				// alert("不需要创建交易")
				//在不需要创建交易的时候，传一个clueId就可以了
				window.location.href="workbench/clue/addConvert.do?clueId=${requestScope.clue.id}&flag=false";
			}
		})
	});
</script>

</head>
<body>
	
	<!-- 搜索市场活动的模态窗口 -->
	<div class="modal fade" id="searchActivityModal" role="dialog" >
		<div class="modal-dialog" role="document" style="width: 90%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title">搜索市场活动</h4>
				</div>
				<div class="modal-body">
					<div class="btn-group" style="position: relative; top: 18%; left: 8px;">
						<form class="form-inline" role="form">
						  <div class="form-group has-feedback">
						    <input type="text" class="form-control" style="width: 300px;" id="searchActivityName" placeholder="请输入市场活动名称，支持模糊查询">
						    <span class="glyphicon glyphicon-search form-control-feedback"></span>
						  </div>
						</form>
					</div>
					<table id="activityTable" class="table table-hover" style="width: 900px; position: relative;top: 10px;">
						<thead>
							<tr style="color: #B3B3B3;">
								<td></td>
								<td>名称</td>
								<td>开始日期</td>
								<td>结束日期</td>
								<td>所有者</td>
								<td></td>
							</tr>
						</thead>
						<tbody id="searchActivityBody">
							<%--<tr>
								<td><input type="radio" name="activity"/></td>
								<td>发传单</td>
								<td>2020-10-10</td>
								<td>2020-10-20</td>
								<td>zhangsan</td>
							</tr>
							<tr>
								<td><input type="radio" name="activity"/></td>
								<td>发传单</td>
								<td>2020-10-10</td>
								<td>2020-10-20</td>
								<td>zhangsan</td>
							</tr>--%>
						</tbody>
					</table>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
					<button type="button" class="btn btn-primary" id="submitActivityBtn">提交</button>
				</div>
			</div>
		</div>
	</div>

	<div id="title" class="page-header" style="position: relative; left: 20px;">
		<input type="hidden" id="hidden-id" value="${requestScope.clue.id}">
		<h4>转换线索 <small>${requestScope.clue.fullname}${requestScope.clue.appellation}-${requestScope.clue.company}</small></h4>
	</div>
	<div id="create-customer" style="position: relative; left: 40px; height: 35px;">
		新建客户：${requestScope.clue.company}
	</div>
	<div id="create-contact" style="position: relative; left: 40px; height: 35px;">
		新建联系人：${requestScope.clue.fullname}${requestScope.clue.appellation}
	</div>
	<div id="create-transaction1" style="position: relative; left: 40px; height: 35px; top: 25px;">
		<input type="checkbox" id="isCreateTransaction"/>
		为客户创建交易
	</div>
	<div id="create-transaction2" style="position: relative; left: 40px; top: 20px; width: 80%; background-color: #F7F7F7; display: none;" >
	
		<form id="tramForm" action="workbench/clue/addConvert.do" method="post">
			<input type="hidden" name="flag" value="true">
			<input type="hidden" name="clueId" value="${requestScope.clue.id}"/>
		  <div class="form-group" style="width: 400px; position: relative; left: 20px;">
		    <label for="amountOfMoney">金额</label>
		    <input type="text" class="form-control" id="amountOfMoney" name="money">
		  </div>
		  <div class="form-group" style="width: 400px;position: relative; left: 20px;">
		    <label for="tradeName">交易名称</label>
		    <input type="text" class="form-control" id="tradeName" name="name">
		  </div>
		  <div class="form-group" style="width: 400px;position: relative; left: 20px;">
		    <label for="expectedClosingDate">预计成交日期</label>
		    <input type="text" class="form-control time" id="expectedClosingDate" name="expectedDate">
		  </div>
		  <div class="form-group" style="width: 400px;position: relative; left: 20px;">
		    <label for="stage">阶段</label>
		    <select id="stage"  class="form-control" name="stage">
		    	<option></option>
				<c:forEach items="${applicationScope.stageList}" var="s">
					<option value="${s.value}">${s.text}</option>
				</c:forEach>
		    </select>
		  </div>
		  <div class="form-group" style="width: 400px;position: relative; left: 20px;">
		    <label for="activity">市场活动源&nbsp;&nbsp;<a href="javascript:void(0);" data-toggle="modal" id="searchActivitySource" data-target="#searchActivityModal" style="text-decoration: none;"><span class="glyphicon glyphicon-search"></span></a></label>
		    <input type="text" class="form-control" id="activityName" placeholder="点击上面搜索" readonly>
		  	<input type="hidden" id="activityId" name="activityId"/>
		  </div>
		</form>
		
	</div>
	
	<div id="owner" style="position: relative; left: 40px; height: 35px; top: 50px;">
		记录的所有者：<br>
		<b>${requestScope.clue.owner}</b>
	</div>
	<div id="operation" style="position: relative; left: 40px; height: 35px; top: 100px;">
		<input class="btn btn-primary" type="button" id="convertBtn" value="转换">
		&nbsp;&nbsp;&nbsp;&nbsp;
		<input class="btn btn-default" type="button" value="取消">
	</div>
</body>
</html>