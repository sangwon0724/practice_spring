<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page session="false" %>
<html>
<head>
	<link href="<c:url value="/resources/css/index.css" />" rel="stylesheet" type="text/css">
	<title>스프링 연습</title>
</head>
<body>
<!-- 네비게이션 시작 -->
		<c:import url="./include/navigation.jsp"></c:import>
<!-- 네비게이션 종료 -->
	<section id="content" class="boardContent">
		<aside class="boardList">
			<!-- 카테고리 목록 시작 -->
				<iframe id="category_iframe" width="100%" height="auto" scrolling="yes" src="/include/boardList"></iframe>
			<!-- 카테고리 목록 종료 -->
		</aside>
		<section class="homeMain">
			메인 홈페이지 내용
		</section>
	</section>
	
	<!-- Scripts -->
	<script src="<c:url value="/resources/js/category.js" />"></script>
</body>
</html>
 