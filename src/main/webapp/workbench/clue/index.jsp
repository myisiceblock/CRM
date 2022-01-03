<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
%>
<!DOCTYPE html>
<html>
<head>
	<base href="<%=basePath%>">
<meta charset="UTF-8">

<link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet" />
<link href="jquery/bootstrap-datetimepicker-master/css/bootstrap-datetimepicker.min.css" type="text/css" rel="stylesheet" />

<script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
<script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/js/bootstrap-datetimepicker.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/locale/bootstrap-datetimepicker.zh-CN.js"></script>
<link rel="stylesheet" type="text/css" href="jquery/bs_pagination/jquery.bs_pagination.min.css">
<script type="text/javascript" src="jquery/bs_pagination/jquery.bs_pagination.min.js"></script>
<script type="text/javascript" src="jquery/bs_pagination/en.js"></script>


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

		pageList(1,10)
		//为创建按钮绑定事件，打开模态窗口
		$("#addBtn").click(function () {

			$.ajax({
				url : "workbench/clue/getUserList.do",
				type : "get",
				dataType : "json",
				success : function (data) {
					/*
						data
							[{用户1},{用户2},{用户3}]
					 */
					var html = "<option></option>"

					$.each(data,function (index,element) {
						html += "<option value='" + element.id + "'>" + element.name + "</option>"
					})

					$("#create-owner").html(html)
					var id = "${user.id}"
					$("#create-owner").val(id)

					//处理完所有者下拉框数据后，打开模态窗口
					$("#createClueModal").modal("show")
				}

			})
		})

		$("#saveBtn").click(function () {
			$.ajax({
				url : "workbench/clue/save.do",
				data : {
					"fullname" : $.trim($("#create-fullname").val()),
					"appellation" : $.trim($("#create-appellation").val()),
					"owner" : $.trim($("#create-owner").val()),
					"company" : $.trim($("#create-company").val()),
					"job" : $.trim($("#create-job").val()),
					"email" : $.trim($("#create-email").val()),
					"phone" : $.trim($("#create-phone").val()),
					"website" : $.trim($("#create-website").val()),
					"mphone" : $.trim($("#create-mphone").val()),
					"state" : $.trim($("#create-state").val()),
					"source" : $.trim($("#create-source").val()),
					"description" : $.trim($("#create-description").val()),
					"contactSummary" : $.trim($("#create-contactSummary").val()),
					"nextContactTime" : $.trim($("#create-nextContactTime").val()),
					"address" : $.trim($("#create-address").val())
				},
				type : "POST",
				dataType : "json",
				success : function (data) {
					if(data.success){
						//如果成功刷新线索列表
						pageList(1,$("#cluePage").bs_pagination('getOption', 'rowsPerPage'));
						//清空新增模态窗口的信息
						$("#clueForm")[0].reset()
						//关闭模态窗口
						$("#createClueModal").modal("hide")
					}else{
						alert("添加失败")
					}
				}
			})
		})

		//为查询按钮绑定事件，触发pageList方法
		$("#searchBtn").click(function () {
			/*
                点击查询按钮的时候，应该将搜索框中的信息保存起来,保存到隐藏域中
             */
			$("#hidden-fullname").val($.trim($("#search-fullname").val()))
			$("#hidden-company").val($.trim($("#search-company").val()))
			$("#hidden-phone").val($.trim($("#search-phone").val()))
			$("#hidden-source").val($.trim($("#search-source").val()))
			$("#hidden-owner").val($.trim($("#search-owner").val()))
			$("#hidden-mphone").val($.trim($("#search-mphone").val()))
			$("#hidden-state").val($.trim($("#search-state").val()))

			pageList(1,10)
		})

		//为全选的复选框绑定事件，触发全选操作
		$("#clueCheckbox").click(function () {
			/*for (var i in activityCheckboxs) {
				// activityCheckboxs[i].checked = this.checked;
			}*/
			$("input[name=clueCheckbox]").prop("checked",this.checked)
		})

		$("#clueTbody").on("click",$("input[name=clueCheckbox]"),function () {
			$("#clueCheckbox").prop("checked",$("input[name=clueCheckbox]").length == $("input[name=clueCheckbox]:checked").length)
		})

		$("#deleteBtn").click(function () {
			let $clueChecked = $("input[name=clueCheckbox]:checked")

			let param = ""
			if($clueChecked.length == 0){
				alert("请选择需要删除的记录！")
			}else{
				if(confirm("确定删除选中的记录？")){

					for (var i = 0;i < $clueChecked.length;i++){
						param += "id="+$($clueChecked[i]).val()

						//如果不是最后一个元素，需要在后面追加一个&符
						if(i < $clueChecked.length - 1){
							param += "&";
						}
					}

					$.ajax({
						url : "workbench/clue/delete.do",
						data : param,
						type : "POST",
						dataType : "json",
						success : function (data) {
							if(data.success){
								// pageList(1,10);
								//删除成功后
								//回到第一页，维持每页展现的记录数
								pageList(1,$("#cluePage").bs_pagination('getOption', 'rowsPerPage'));

							}else{
								alert('删除线索失败')
							}
						}

					})
				}
			}
		})

		$("#editBtn").click(function () {
			let $clueChecked = $("input[name=clueCheckbox]:checked")

			if($clueChecked.length == 0){
				alert("请选择需要修改的记录")
			}else if ($clueChecked.length > 1){
				alert("只能选择一条记录修改")
			}else{
				let id = $clueChecked.val()
				$.ajax({
					url : "workbench/clue/getUserListAndClue.do",
					data : {
						"id" : id
					},
					type : "get",
					dataType : "json",
					success : function (data) {
						/*
                            data
                                用户列表
                                市场活动对象
                                {"userList":[{用户1},{2},{3}],"clue":{市场活动}}
                         */
						//处理所有者的下拉框
						let html = "<option></option>";
						$.each(data.userList,function(index,element){
							html += "<option value='" + element.id + "'>" + element.name + "</option>";
						})

						$("#edit-owner").html(html);

						//处理单条clue
						$("#edit-id").val(data.clue.id)
						$("#edit-owner").val(data.clue.owner)
						$("#edit-description").val(data.clue.description)
						$("#edit-fullname").val(data.clue.fullname)
						$("#edit-company").val(data.clue.company)
						$("#edit-email").val(data.clue.email)
						$("#edit-website").val(data.clue.website)
						$("#edit-phone").val(data.clue.phone)
						$("#edit-mphone").val(data.clue.mphone)
						$("#edit-state").val(data.clue.state)
						$("#edit-source").val(data.clue.source)
						$("#edit-job").val(data.clue.job)
						$("#edit-contactSummary").val(data.clue.contactSummary)
						$("#edit-nextContactTime").val(data.clue.nextContactTime)
						$("#edit-address").val(data.clue.address)
						$("#edit-appellation").val(data.clue.appellation)

						//所有值都填写好之后，打开修改操作的模态窗口
						$("#editClueModal").modal("show");
					}
				})
			}
		})

		$("#updateBtn").click(function(){
			$.ajax({
				url : "workbench/clue/update.do",
				type : "POST",
				data : {
					"id" : $.trim($("#edit-id").val()),
					"fullname" : $.trim($("#edit-fullname").val()),
					"appellation" : $.trim($("#edit-appellation").val()),
					"owner" : $.trim($("#edit-owner").val()),
					"company" : $.trim($("#edit-company").val()),
					"job" : $.trim($("#edit-job").val()),
					"email" : $.trim($("#edit-email").val()),
					"phone" : $.trim($("#edit-phone").val()),
					"website" : $.trim($("#edit-website").val()),
					"mphone" : $.trim($("#edit-mphone").val()),
					"state" : $.trim($("#edit-state").val()),
					"source" : $.trim($("#edit-source").val()),
					"description" : $.trim($("#edit-description").val()),
					"contactSummary" : $.trim($("#edit-contactSummary").val()),
					"nextContactTime" : $.trim($("#edit-nextContactTime").val()),
					"address" : $.trim($("#edit-address").val())
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
						pageList($("#cluePage").bs_pagination('getOption', 'currentPage')
								,$("#cluePage").bs_pagination('getOption', 'rowsPerPage'));

						//清空添加操作模态窗口中的数据
						//这个方法用不了
						// $("#activityAddForm").reset()
						// $("#activityAddForm")[0].reset()
						//关闭添加操作的模态窗口
						$("#editClueModal").modal("hide")
					}else{
						alert("修改市场活动失败")
					}
				}
			})
		})
	});

	function pageList(pageNo,pageSize) {

		//将全选的复选框的勾干掉
		$("#clueCheckbox").prop("checked",false)

		//查询前，将隐藏域中保存的信息取出来，重新赋予到搜索框中
		$("#search-fullname").val($.trim($("#hidden-fullname").val()))
		$("#search-company").val($.trim($("#hidden-company").val()))
		$("#search-phone").val($.trim($("#hidden-phone").val()))
		$("#search-source").val($.trim($("#hidden-source").val()))
		$("#search-owner").val($.trim($("#hidden-owner").val()))
		$("#search-mphone").val($.trim($("#hidden-mphone").val()))
		$("#search-state").val($.trim($("#hidden-state").val()))

		$.ajax({
			url : "workbench/clue/pageList.do",
			data : {
				"pageNo" : pageNo,
				"pageSize" : pageSize,
				"fullname" : $.trim($("#search-fullname").val()),
				"company" : $.trim($("#search-company").val()),
				"phone" : $.trim($("#search-phone").val()),
				"source" : $.trim($("#search-source").val()),
				"owner" : $.trim($("#search-owner").val()),
				"mphone" : $.trim($("#search-mphone").val()),
				"state" : $.trim($("#search-state").val())
			},
			type : "get",
			dataType : "json",
			success : function (data) {

				var html = "";

				//每一个element就是每一个市场活动对象
				$.each(data.dataList,function(index,element){
					html += '<tr>'
					html += '<td><input type="checkbox" name="clueCheckbox" value="' + element.id + '"/></td>'
					html += '<td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href=\'workbench/clue/detail.do?id=' + element.id + '\';">' + element.fullname + element.appellation + '</a></td>'
					html += '<td>' + element.company + '</td>'
					html += '<td>' + element.phone + '</td>'
					html += '<td>' + element.mphone + '</td>'
					html += '<td>' + element.source + '</td>'
					html += '<td>' + element.owner + '</td>'
					html += '<td>' + element.state + '</td>'
					html += '</tr>'
				})

				$("#clueTbody").html(html)

				//计算总页数
				var totalPages = data.total % pageSize == 0 ? data.total/pageSize : parseInt(data.total/pageSize) + 1

				//数据处理完毕后，结合分页查询，对前端展现分页信息
				$("#cluePage").bs_pagination({
					currentPage: pageNo, // 页码
					rowsPerPage: pageSize, // 每页显示的记录条数
					maxRowsPerPage: 20, // 每页最多显示的记录条数
					totalPages: totalPages, // 总页数
					totalRows: data.total, // 总记录条数

					visiblePageLinks: 10, // 显示几个卡片

					showGoToPage: true,
					showRowsPerPage: true,
					showRowsInfo: true,
					showRowsDefaultInfo: true,

					onChangePage : function(event, data){
						pageList(data.currentPage , data.rowsPerPage);
					}
				});
			}
		})
	}
	
</script>
</head>
<body>
	<input type="hidden" id="hidden-fullname"/>
	<input type="hidden" id="hidden-company"/>
	<input type="hidden" id="hidden-phone"/>
	<input type="hidden" id="hidden-source"/>
	<input type="hidden" id="hidden-owner"/>
	<input type="hidden" id="hidden-mphone"/>
	<input type="hidden" id="hidden-state"/>
	<!-- 创建线索的模态窗口 -->
	<div class="modal fade" id="createClueModal" role="dialog">
		<div class="modal-dialog" role="document" style="width: 90%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLabel">创建线索</h4>
				</div>
				<div class="modal-body">
					<form class="form-horizontal" id="clueForm" role="form">
					
						<div class="form-group">
							<label for="create-clueOwner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="create-owner">
								</select>
							</div>
							<label for="create-company" class="col-sm-2 control-label">公司<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="create-company">
							</div>
						</div>
						
						<div class="form-group">
							<label for="create-call" class="col-sm-2 control-label">称呼</label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="create-appellation">
									<option></option>
								  	<c:forEach items="${appellationList}" var="a">
										<option value="${a.value}">${a.text}</option>
									</c:forEach>
								</select>
							</div>
							<label for="create-surname" class="col-sm-2 control-label">姓名<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="create-fullname">
							</div>
						</div>
						
						<div class="form-group">
							<label for="create-job" class="col-sm-2 control-label">职位</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="create-job">
							</div>
							<label for="create-email" class="col-sm-2 control-label">邮箱</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="create-email">
							</div>
						</div>
						
						<div class="form-group">
							<label for="create-phone" class="col-sm-2 control-label">公司座机</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="create-phone">
							</div>
							<label for="create-website" class="col-sm-2 control-label">公司网站</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="create-website">
							</div>
						</div>
						
						<div class="form-group">
							<label for="create-mphone" class="col-sm-2 control-label">手机</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="create-mphone">
							</div>
							<label for="create-status" class="col-sm-2 control-label">线索状态</label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="create-state">
								  <option></option>
									<c:forEach items="${clueStateList}" var="c">
										<option value="${c.value}">${c.text}</option>
									</c:forEach>
								</select>
							</div>
						</div>
						
						<div class="form-group">
							<label for="create-source" class="col-sm-2 control-label">线索来源</label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="create-source">
								  <option></option>
									<c:forEach items="${sourceList}" var="s">
										<option value="${s.value}">${s.text}</option>
									</c:forEach>
								</select>
							</div>
						</div>
						

						<div class="form-group">
							<label for="create-describe" class="col-sm-2 control-label">线索描述</label>
							<div class="col-sm-10" style="width: 81%;">
								<textarea class="form-control" rows="3" id="create-description"></textarea>
							</div>
						</div>
						
						<div style="height: 1px; width: 103%; background-color: #D5D5D5; left: -13px; position: relative;"></div>
						
						<div style="position: relative;top: 15px;">
							<div class="form-group">
								<label for="create-contactSummary" class="col-sm-2 control-label">联系纪要</label>
								<div class="col-sm-10" style="width: 81%;">
									<textarea class="form-control" rows="3" id="create-contactSummary"></textarea>
								</div>
							</div>
							<div class="form-group">
								<label for="create-nextContactTime" class="col-sm-2 control-label">下次联系时间</label>
								<div class="col-sm-10" style="width: 300px;">
									<input type="text" class="form-control time" id="create-nextContactTime">
								</div>
							</div>
						</div>
						
						<div style="height: 1px; width: 103%; background-color: #D5D5D5; left: -13px; position: relative; top : 10px;"></div>
						
						<div style="position: relative;top: 20px;">
							<div class="form-group">
                                <label for="create-address" class="col-sm-2 control-label">详细地址</label>
                                <div class="col-sm-10" style="width: 81%;">
                                    <textarea class="form-control" rows="1" id="create-address"></textarea>
                                </div>
							</div>
						</div>
					</form>
					
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" id="saveBtn">保存</button>
				</div>
			</div>
		</div>
	</div>
	
	<!-- 修改线索的模态窗口 -->
	<div class="modal fade" id="editClueModal" role="dialog">

		<div class="modal-dialog" role="document" style="width: 90%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title">修改线索</h4>
				</div>
				<div class="modal-body">
					<form class="form-horizontal" role="form">
						<input type="hidden" id="edit-id">
						<div class="form-group">
							<label for="edit-clueOwner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="edit-owner">
									<option></option>
								</select>
							</div>
							<label for="edit-company" class="col-sm-2 control-label">公司<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="edit-company" value="动力节点">
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-call" class="col-sm-2 control-label">称呼</label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="edit-appellation">
								  	<option></option>
									<c:forEach items="${appellationList}" var="a">
										<option value="${a.value}">${a.text}</option>
									</c:forEach>
								</select>
							</div>
							<label for="edit-surname" class="col-sm-2 control-label">姓名<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="edit-fullname" value="李四">
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-job" class="col-sm-2 control-label">职位</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="edit-job" value="CTO">
							</div>
							<label for="edit-email" class="col-sm-2 control-label">邮箱</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="edit-email" value="lisi@bjpowernode.com">
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-phone" class="col-sm-2 control-label">公司座机</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="edit-phone" value="010-84846003">
							</div>
							<label for="edit-website" class="col-sm-2 control-label">公司网站</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="edit-website" value="http://www.bjpowernode.com">
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-mphone" class="col-sm-2 control-label">手机</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="edit-mphone" value="12345678901">
							</div>
							<label for="edit-status" class="col-sm-2 control-label">线索状态</label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="edit-state">
								 	 <option></option>
									<c:forEach items="${clueStateList}" var="c">
										<option value="${c.value}">${c.text}</option>
									</c:forEach>
								</select>
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-source" class="col-sm-2 control-label">线索来源</label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="edit-source">
								  	<option></option>
									<c:forEach items="${sourceList}" var="s">
										<option value="${s.value}">${s.text}</option>
									</c:forEach>
								</select>
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-describe" class="col-sm-2 control-label">描述</label>
							<div class="col-sm-10" style="width: 81%;">
								<textarea class="form-control" rows="3" id="edit-description"></textarea>
							</div>
						</div>
						
						<div style="height: 1px; width: 103%; background-color: #D5D5D5; left: -13px; position: relative;"></div>
						
						<div style="position: relative;top: 15px;">
							<div class="form-group">
								<label for="edit-contactSummary" class="col-sm-2 control-label">联系纪要</label>
								<div class="col-sm-10" style="width: 81%;">
									<textarea class="form-control" rows="3" id="edit-contactSummary"></textarea>
								</div>
							</div>
							<div class="form-group">
								<label for="edit-nextContactTime" class="col-sm-2 control-label">下次联系时间</label>
								<div class="col-sm-10" style="width: 300px;">
									<input type="text" class="form-control" id="edit-nextContactTime">
								</div>
							</div>
						</div>
						
						<div style="height: 1px; width: 103%; background-color: #D5D5D5; left: -13px; position: relative; top : 10px;"></div>

                        <div style="position: relative;top: 20px;">
                            <div class="form-group">
                                <label for="edit-address" class="col-sm-2 control-label">详细地址</label>
                                <div class="col-sm-10" style="width: 81%;">
                                    <textarea class="form-control" rows="1" id="edit-address"></textarea>
                                </div>
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
	
	
	
	
	<div>
		<div style="position: relative; left: 10px; top: -10px;">
			<div class="page-header">
				<h3>线索列表</h3>
			</div>
		</div>
	</div>
	
	<div style="position: relative; top: -20px; left: 0px; width: 100%; height: 100%;">
	
		<div style="width: 100%; position: absolute;top: 5px; left: 10px;">
		
			<div class="btn-toolbar" role="toolbar" style="height: 80px;">
				<form class="form-inline" role="form" style="position: relative;top: 8%; left: 5px;">
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">名称</div>
				      <input class="form-control" type="text" id="search-fullname">
				    </div>
				  </div>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">公司</div>
				      <input class="form-control" type="text" id="search-company">
				    </div>
				  </div>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">公司座机</div>
				      <input class="form-control" type="text" id="search-phone">
				    </div>
				  </div>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">线索来源</div>
					  <select class="form-control" id="search-source">
						  <option></option>
						  <c:forEach items="${sourceList}" var="s">
							  <option value="${s.value}">${s.text}</option>
						  </c:forEach>
					  </select>
				    </div>
				  </div>
				  
				  <br>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">所有者</div>
				      <input class="form-control" type="text" id="search-owner">
				    </div>
				  </div>
				  
				  
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">手机</div>
				      <input class="form-control" type="text" id="search-mphone">
				    </div>
				  </div>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">线索状态</div>
					  <select class="form-control" id="search-state">
					  	<option></option>
						  <c:forEach items="${clueStateList}" var="c">
							  <option value="${c.value}">${c.text}</option>
						  </c:forEach>
					  </select>
				    </div>
				  </div>

				  <button type="button" class="btn btn-default" id="searchBtn">查询</button>
				  
				</form>
			</div>
			<div class="btn-toolbar" role="toolbar" style="background-color: #F7F7F7; height: 50px; position: relative;top: 40px;">
				<div class="btn-group" style="position: relative; top: 18%;">
				  <button type="button" class="btn btn-primary" id="addBtn"><span class="glyphicon glyphicon-plus"></span> 创建</button>
				  <button type="button" class="btn btn-default" id="editBtn"><span class="glyphicon glyphicon-pencil"></span> 修改</button>
				  <button type="button" class="btn btn-danger" id="deleteBtn"><span class="glyphicon glyphicon-minus"></span> 删除</button>
				</div>
				
				
			</div>
			<div style="position: relative;top: 50px;">
				<table class="table table-hover">
					<thead>
						<tr style="color: #B3B3B3;">
							<td><input type="checkbox" id="clueCheckbox"/></td>
							<td>名称</td>
							<td>公司</td>
							<td>公司座机</td>
							<td>手机</td>
							<td>线索来源</td>
							<td>所有者</td>
							<td>线索状态</td>
						</tr>
					</thead>
					<tbody id="clueTbody">
						<%--<tr>
							<td><input type="checkbox" /></td>
							<td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href='/clue/detail.jsp';">李四先生</a></td>
							<td>动力节点</td>
							<td>010-84846003</td>
							<td>12345678901</td>
							<td>广告</td>
							<td>zhangsan</td>
							<td>已联系</td>
						</tr>
                        <tr class="active">
                            <td><input type="checkbox" /></td>
                            <td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href='/workbench/clue/detail.jsp';">李四先生</a></td>
                            <td>动力节点</td>
                            <td>010-84846003</td>
                            <td>12345678901</td>
                            <td>广告</td>
                            <td>zhangsan</td>
                            <td>已联系</td>
                        </tr>--%>
					</tbody>
				</table>
			</div>
			
			<div style="height: 50px; position: relative;top: 60px;">
				<div id="cluePage" style="position: fixed; bottom: 0; left: 0; right: 0"></div>
			</div>
			
		</div>
		
	</div>
</body>
</html>