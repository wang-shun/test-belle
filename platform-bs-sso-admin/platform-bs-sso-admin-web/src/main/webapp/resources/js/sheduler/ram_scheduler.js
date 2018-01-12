function pause() {
	var row = $("#scheduler").datagrid('getSelected');
	if (row == null) {
		$.messager.alert('警告', '请选择暂停触发器');
		return;
	}
	var data = {};
	data.name = row.triggerName;
	data.group = row.triggerGroup;
	data._method = "put";

	$.messager.confirm('确认', '您确认想要暂停触发器吗？', function(r) {
		if (r) {
			$.ajax({
				url : BasePath + '/scheduler/ram/pause',
				type : 'post',
				data : data,
				success : function(d) {
					if (d.success == "true") {
						$('#scheduler').datagrid('reload');
					} else {
						$.messager.alert('警告', '暂停失败');
					}
				}
			});
		}
	});
}

function resume() {
	var row = $("#scheduler").datagrid('getSelected');
	if (row == null) {
		$.messager.alert('警告', '请选择恢复的触发器');
		return;
	}
	var data = {};
	data.name = row.triggerName;
	data.group = row.triggerGroup;
	data._method = "put";

	$.messager.confirm('确认', '您确认想要恢复触发器吗？', function(r) {
		if (r) {
			$.ajax({
				url : BasePath + '/scheduler/ram/resume',
				type : 'post',
				data : data,
				success : function(d) {
					if (d.success == "true") {
						$('#scheduler').datagrid('reload');
					} else {
						$.messager.alert('警告', '恢复失败');
					}
				}
			});
		}
	});
}
function batchAdd(){
	var d=$('<div/>').appendTo("body");
	var c="<iframe width='100%' height='100%' src='"+BasePath+'/scheduler/cron/batch'+"'/>"
	$(d).window({width:900,height:350,title:"批量添加业务调度(隐藏功能)",content:c,modal:true,maximizable:true,minimizable:false}); 
}

function batchUpdateUrl() {
	
	$("#saveEdit").unbind("click");
	$("#saveEdit").bind("click", batchUpdateUrl_save);
	
	$('#dlg').dialog('open').dialog('setTitle','批量初始化url');
	
}

function batchUpdateUrl_save(){
	//更改url
	var flag=$("#fm").form('validate');
	var data=$('#fm').serialize();
		data+="&_method=put";
	var url= BasePath + "/scheduler/batchUpdateUrl";
	save_Editdata(flag,url,data);
}

function save_Editdata(flag,url,data){	
	if(flag){
		$.easyui.loading({
			topMost: false,
			msg: "批量修改中..稍等片刻"
		});
		$.ajax({
			url: url,
			type:'post',
			data: data,
			success: function(d){
				if(d.success=="true"){
					$('#dlg').dialog('close');
					$('#scheduler').datagrid('reload');
					$.messager.alert('提示','批量修改url成功');
				}else{
					$.messager.alert('警告','批量修改url失败');
				}
				$.easyui.loaded();
			}
	   	});
	}
}


$(function($) {
	$("#pause").click(pause);
	$("#resume").click(resume);
	$("#batchAdd").click(batchAdd);
	$("#batchUpdateUrl").click(batchUpdateUrl);
});
$(window)
		.load(
				function() {
					$(window).resize(function() {
						$('#scheduler').datagrid('resize', {
							width : document.body.clientWidth,
							height : document.body.clientHeight - 30
						});
					});
					// 加入自刷新
					var tr = $("[class='datagrid-pager pagination']").find(
							"table tr");
					var s = $('<select><option value="-1" selected>不刷新</option><option value="5">5秒</option><option value="10">10秒</option><option value="15">15秒</option><option value="20">20秒</option><option value="30">30秒</option></select>')
					s.change(function() {
						if ($(this).val() > 0) {
							if (!!intervalId)
								clearInterval(intervalId);
							intervalId = setInterval(function() {
								$('#scheduler').datagrid('reload');
							}, $(this).val() * 1000);
						} else {
							clearInterval(intervalId);
						}
					});
					var td = $('<td><span style="margin-left:5px;">自动刷新:</span></td>');
					s.appendTo(td.find("span"));
					td.appendTo(tr);
				});