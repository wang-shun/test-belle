<%--

    Licensed to Jasig under one or more contributor license
    agreements. See the NOTICE file distributed with this work
    for additional information regarding copyright ownership.
    Jasig licenses this file to you under the Apache License,
    Version 2.0 (the "License"); you may not use this file
    except in compliance with the License.  You may obtain a
    copy of the License at the following location:

      http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied.  See the License for the
    specific language governing permissions and limitations
    under the License.

--%>  <!--[if lt IE 9]>
    <script src="//cdnjs.cloudflare.com/ajax/libs/html5shiv/3.6.1/html5shiv.js" type="text/javascript"></script>
  <![endif]-->
<!DOCTYPE html>

<%@ page pageEncoding="UTF-8" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta http-equiv="X-UA-Compatible" content="ie=edge">
  <title>百丽统一登录平台</title>
  
  <link rel="icon" href="<c:url value="/favicon.ico" />" type="image/x-icon" />

  <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/bootstrap.min.css" /> 
  <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/iconFont/iconfont.css" />
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/login.css" />   
    
  <script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.min.js"></script>
  <script type="text/javascript" src="${pageContext.request.contextPath}/js/index.js"></script> 
  <script type="text/javascript" src="${pageContext.request.contextPath}/js/popper.min.js" ></script>
  <script type="text/javascript" src="${pageContext.request.contextPath}/js/bootstrap.min.js" ></script>
  
  <!-- Lodop设备加载js文件 -->
  <script type="text/javascript" src="${pageContext.request.contextPath}/js/CLodopFuncs.js"></script>
  <script type="text/javascript" src="${pageContext.request.contextPath}/js/pc_deviceinfo.js" ></script>
  <!-- 
  <script type="text/javascript" src="${pageContext.request.contextPath}/js/CLodopfuncs_info.js" ></script>
   -->
  <object  id="LODOP_OB" classid="clsid:2105C259-1E0C-4534-8141-A753534CB4CA" width=0 height=0> 
       <embed id="LODOP_EM" type="application/x-print-lodop" width=0 height=0></embed>
  </object>
  
</head>

<body id="cas">
