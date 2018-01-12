var statistics_chart = {};

/**
 * 将指定id的div初始化为统计图
 * 
 * @author yang.wei
 * @date 2017-02-28
 */
statistics_chart.init = function() {
	var chart = echarts.init(document.getElementById("statistics_chart"));
	statistics_chart.chart = chart;

	var option = {
		title : {
			text : ''
		},
		legend: {
	        data:['失败','成功']
	    },
		tooltip : {
			trigger : 'axis'
		},
		toolbox : {
			show : true,
			feature : {
				mark : {
					show : true
				},
				dataView : {
					show : true,
					readOnly : false
				},
				magicType : {
					show : true,
					type : [ 'line', 'bar' ]
				},
				restore : {
					show : true
				},
				saveAsImage : {
					show : true
				}
			}
		},
		xAxis : {
			name : '时间',
			data : []
		},
		yAxis : {
			name : '数量'
		},
		dataZoom : [ {
			type : 'slider',
			xAxisIndex : 0,
			start : 0,
			end : 100
		}, {
			type : 'inside',
			xAxisIndex : 0,
			start : 0,
			end : 100
		}, {
			type : 'slider',
			yAxisIndex : 0,
			start : 0,
			end : 100
		} ],
		series : [ {
			name : '失败',
			type : 'bar',
			data : []
			},{
			name : '成功',
			type : 'bar',
			data : []
		} ]
	};
	chart.setOption(option);
};

/**
 * 远程获取并加载统计数据
 * 
 * @author yang.wei
 * @date 2017-02-28
 */
statistics_chart.load = function() {
	$.ajax({
		type : 'GET',
		url : BasePath + '/statistics/data',
		data : {
			"type" : $("#type").combobox('getValue'),
			"format" : $("#format").combobox('getValue'),
			"bizType" : $("#bizType").combobox('getValue'),
			"startDate" : $("#startDate").datebox('getValue'),
			"endDate" : $("#endDate").datebox('getValue')
		},
		success : function(result) {
			console.log(result.message);//有message信息，表示后台有错
			statistics_chart.chart.setOption({
				xAxis : {
					data : result.xData
				},
				series : [ {
					data : result.zData
					},{
					data : result.yData
				} ]
			});
		}
	});
};

//初始化业务类型下拉框
statistics_chart.initBizTypeList = function() {
	$("#bizType").combobox({
	    url:BasePath + '/biz/type/list',
	    method: 'get',
	    data: {rows: 100},
	    loadFilter: function(data){
	    	return data.rows;
	    },
	    valueField:'bizNo',
	    textField:'bizName'
	});
};

$(document).ready(function() {
	statistics_chart.initBizTypeList();
	statistics_chart.init();
	statistics_chart.load();
});