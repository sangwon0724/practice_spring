<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>작성 테스트</title>
</head>
<body>
	<form method="post">
		<input type="text" name="testText" value="<c:out value="${testVO.test}" escapeXml="false"/>">
		<input type="submit">
	</form>
            <a href="/">home으로 이동</a>
</body>
</html>