var ctrl = {}; 
$(function() {
	initOrgUnit();
});
 


function init_tree(id, url, fn) {
	$('#' + id).tree({
		url : url,
		checkbox : true, 
		cascadeCheck: false, //不级联选择
		onlyLeafCheck : false,
		lines : true,// 显示虚线效果 
		onClick : fn,  
		onSelect: function (node) {
            var cknodes = $('#' + id).tree("getChecked");
            for (var i = 0; i < cknodes.length; i++) {
                if (cknodes[i].id != node.id) {
                    $('#' + id).tree("uncheck", cknodes[i].target);
                }
            }
            if (node.checked) {
                $('#' + id).tree('uncheck', node.target);

            } else {
                $('#' + id).tree('check', node.target);
            }
        },
		onLoadSuccess : function(node, data){
			$(this).find('span.tree-checkbox').unbind().click(function () {
                $('#' + id).tree('select', $(this).parent());
                return false;
            });
			/*if(data!=null){
				var res = data.map(function (node,index,input) {
					initTreeState(node);
				})
			}*/
		}
	});
}

function initTreeState(node){
	if(node==null) return;
	//设置只能选择页子节点
	/*if(node.isLeaf==false){
		$("#"+node.domId).removeClass("tree-node-selected"); 
		$("#"+node.domId).find("span").removeClass("tree-checkbox1").removeClass("tree-checkbox");
	}*/
	var subArr = node.children;
	$(subArr).each(function (index, obj) {
		initTreeState(obj);
    });
}

 
function initLeftTree() {
	var url = BasePath + '/org_unit/tree_data_pre.json?refresh=1&level=1';
	init_tree('org-unit-tree', url, function(node) {  
		if(node.state=='closed')
		{
			$(this).tree('expand', node.target);
		}
		ctrl.edit(node.id);
		$('#unit_search').val(node.text);
		do_tree_search(node.id);
	});
}

function initSsoUserTree(organizationCode) {
	var url = BasePath + '/org_unit/tree_data_pre.json?refresh=1&level=1&organizationCode='+organizationCode;
	init_tree('org-unit-tree', url, function(node) {  
		if(node.state=='closed')
		{
			$(this).tree('expand', node.target);
		}
		ctrl.edit(node.id);
		$('#unit_search').val(node.text);
		do_tree_search(node.id);
	});
}

function initOrgUnit(){
	$('#org_cond').combotree(
			{
				url : BasePath + '/org_unit/tree_data_pre.json?refresh=1&level=1',
				valueField : 'id',
				textField : 'text',
				width : 200,
				editable : false,
				searchBox:true,
				onSelect : function() {
					$.ajax({
						url : BasePath + '/org_unit/tree_data_pre.json?refresh=1&level=1',
						type : 'post',
						statusCode : {
							500 : function() {
								$.messager.alert('警告', '保存错误，编号500,请稍后重试');
							}
						}
					})
				},
				onLoadSuccess:function(node,data){  
					var tree = $('#org_cond').combotree('tree');
					var defaultNode = tree.tree('find',data[0].id);	
					$("#org_cond").combotree('setValue',defaultNode.id);
					tree.tree('select',defaultNode.target);  
					$('#searchForm').form('clear');
				}
			});
}
