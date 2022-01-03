<%--
  Created by IntelliJ IDEA.
  User: lyb20
  Date: 2021/12/22
  Time: 10:42
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
        $.ajax({
        url : "",
        data : {

        },
        type : "POST",
        dataType : "json",
        success : function (data) {

        }

        })

        String createTime = DateTimeUtil.getSysTime();
        String createBy = ((User) request.getSession().getAttribute("user")).getName();

        $(".time").datetimepicker({
        minView: "month",
        language:  'zh-CN',
        format: 'yyyy-mm-dd',
        autoclose: true,
        todayBtn: true,
        pickerPosition: "bottom-left",
        readOnly : true
        });
</body>
</html>
