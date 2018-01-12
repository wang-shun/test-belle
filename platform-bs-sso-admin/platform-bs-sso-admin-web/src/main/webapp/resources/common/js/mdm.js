/// <reference path="~/Scripts/jquery-1.8.3.min.js" />
var mdm = function () {
    var categoryService = function () {

        function getUrl(categoryId, parentCategoryId, parentCategoryEntryCode) {
            var url = $.fn.category.url + 'get_biz?lookupId=' + categoryId;
            if (parentCategoryId) {
                url = $.fn.category.url + 'get_child_entry_bycode?lookupId='
						+ parentCategoryId + '&subLookupId=' + self.categoryId
						+ "&code=" + parentCategoryEntryCode;
            }

            return url;
        };

        var getItems = function (category, textField, valueField,wraped) {
            return getChildItems(category, null, null, textField, valueField, wraped);
        };

        var getChildItems = function (category, parentCategory,
				parentCategoryEntryCode, textField, valueField, wraped) {
            var url = getUrl(category, parentCategory, parentCategoryEntryCode);
            textField = textField ? textField : "name";
            valueField = valueField ? valueField : "code";
            var items = null;//
            if (!wraped)
                items = {};
            else
                items = [];
            $.ajax({
                url: url,
                async: false
            }).then(function (data) {
                $.each(data, function (i, n) {
                    if (!wraped)
                        items[n.code] = n.name;
                    else {
                        var obj = {};
                        obj[valueField] = n.code,
                        obj[textField] = n.name,
                        items.push(obj)
                    }
                });
            });
            return items;
        };

        var batchGetItems = function (categories, textField, valueField) {
            var items = {};
            $.each(categories, function (i, n) {
                items[n] = getItems(n, textField, valueField);
            });
            return items;
        };

        var wrap = function (data, textField, valueField) {
            var result = [];
            $.each(data, function (i, row) {
                var item = {};
                item["code"] = row[valueField];
                item["name"] = row[textField];
                item["data"] = row;
                result.push(item);
            });
            return result;
        }

        var service = {
            getItems: getItems,
            batchGetItems: batchGetItems,
            wrap: wrap
        };
        return service;
    }();
    var mdm = {
        service: {
            categoryService: categoryService
        }
    };
    return mdm;
}();
mdm.controller = function () {
    var self = this;

    //网格选择的行
    this.selectedRow = null;
    //网格选择的行索引
    this.selectedRowIndex = null;
    //页面指定的grid
    this.grid = $('#grid');
    //查询表单
    this.searchForm = $('#searchForm');
    //编辑表单
    this.editForm = $('#editForm');
    //编辑Panel
    this.editPanel = $('#showDialog');
    //主键字段
    this.premaryField = 'id';

    this.panelOptions = { height: 220 };
    /**
    加载表格数据
    **/
    this.loadGridData = function () {
        var data = this.searchForm.form('getData');
        this.grid.datagrid('options').url = 'list.json?status=1';
        this.grid.datagrid('options').queryParams = data;
        this.grid.datagrid('load');
    };
    /**
    清除编辑表单信息
    **/
    this.clear = function () {
        this.editForm.form('clear');
    }
    /**
    初始化
    **/
    this.init = function () {
        console.log('controller initail....');
    };

    /**
    验证待提交数据是否完整
    **/
    this.validate = function () {
        return this.editForm.form('validate');
    }

    var getLabelText = function (node) {
        var i = 0;
        var parent = null;
        while (true) {
            parent = $(node).parent();
            if (parent[0].nodeName == "TD") {
                var text = parent.prev('th').text();
                var reg = /[\u4E00-\u9FA5\uf900-\ufa2d]+/ig;
                var val = reg.exec(text);

                return val;
            }
            if (i++ > 25)
                return '';
        }
    }
    /**
    *唯一性校验，暂只支持文本控件
    **/
    this.uniqueCheck = function (data) {
        var url = 'get_biz?';
        var param = {};
        var nodes = this.editForm.find('.unique');
        var self = this;
        for (var i = 0; i < nodes.length; i++) {
            var node = $(nodes[i]);
            var ok = function (node) {
                var name = node.attr("name");
                var value = node.val();
                var checked = false;
                $.ajax({
                    url: url + name + "=" + value,
                    async: false,
                    success: function (result) {
                        checked = self.uniqueChecking(name, result);
                        if (!checked) {
                            var text = getLabelText(node);
                            node.focus();
                            showError(text + "已存在!");
                        }
                    }
                });
                return checked;
            }(node);
            if (!ok) {
                return false;
            }
        }
        return true;
    };

    /**
    *按照属性检查重复性
    *@property 属性名称
    *@来源于页面输入
    *@remote 服务器查询返回结果
    **/
    this.uniqueChecking = function (property, remote) {
        if (!remote || remote.length == 0)
            return true;
        if (remote.length > 1) //超过两条肯定重复
            return false;
        var item = remote[0];
        var value = this.editForm.find('input[name="' + this.premaryField + '"]').val();
        //不是自身则重复
        return item[this.premaryField] == value;
    };

    /** 显示编辑框 
    *@options 编辑框options
    *@callback 点击保存按钮时的回调
    **/
    this.showPanel = function (options, callback) {
        options.ok = callback;
        //options.height = 220;
        this.panelOptions = $.extend(this.panelOptions, options);
        this.editPanel.showpanel(this.panelOptions);
    }

    /*
    *获取保存提交后台url
    */
    this.getSaveUrl = function () {
        return 'update';
    };

    /*
    *预保存处理
    */
    this.preSave = function (data) {

    };

    /*
    *保存完成事件
    */
    this.afterSave = function (result) {
        this.refreshGrid(result);
    };

    /*
    *更新网格
    */
    this.refreshGrid = function (result) {
        var id = $('#' + this.premaryField).val();
        if (id) {
            $('#grid').datagrid('updateRow', {
                index: this.selectedRowIndex,
                row: result
            });
        } else {
            this.loadGridData();
        }
    };
    /*
    *保存成功回调
    */
    this.saved = function () {
        showSuc('保存成功!');
    };

    /*
    *保存失败回调
    */
    this.saveFail = function () {
        showError('保存失败,请联系管理员!');
    };

    /*
    *保存完成回调，不论保存是否成功该方法都会被调用
    */
    this.saveComplete = function () {
        this.clear();
    };

    /*
    *执行保存
    */
    this.save = function (model) {
        model = model || {};
        var url = this.getSaveUrl();
        var selft = this;
        return this.editForm.form('post', {
            url: url,
            onSubmit: function (data) {
                data = $.extend(model, data);
                self.preSave(data);
                return data;
            }
        }).then(function (data) {
            self.afterSave(data);
        })
        .then(function () { self.saved(); })
        .fail(function () { self.saveFail(); })
        .done(function () { self.saveComplete(); });
    }

    /*
    准备新增
    */
    this.preAdd = function () {
        console.log('pre add');
    };
    /**
    *新增
    **/
    this.add = function () {
        this.selectedRow = null;
        this.selectedRowIndex = -1;
        this.clear();
        this.preAdd();
        var selft = this;
        this.showPanel({title: "新增"}, function (dialog) {
            if (!self.validate() || !self.uniqueCheck())
                return;
            self.save().then(function () { dialog.close() });
        });
    };

    /*
    *获取编辑时查询具体项的url
    **/
    this.getEditUrl = function (data) {
        return "get?" + this.premaryField + "=" + data[this.premaryField];
    };

    /**预编辑
    *@result 编辑前通过查询后端返回的对象
    **/
    this.preEdit = function (result) {
        this.selectedRow = $.extend(this.selectedRow, result);
        //return this.selectedRow;
        return result;
    };

    /**
    *编辑
    **/
    this.edit = function (rowIndex, row) {
        this.clear();
        this.selectedRow = row;
        this.selectedRowIndex = rowIndex;

        var url = this.getEditUrl(row);
        var self = this;
        this.editForm.form('load', url)
            .then(function (result) {
                return self.preEdit(result) || result;
            })
            .then(function (data) {
                self.showPanel({
                    title: "编辑"
                }, function (dialog) {
                    if (!self.validate() || !self.uniqueCheck(data))
                        return;
                    self.save(data).then(function () {
                        dialog.close();
                    });
                });
            });
    };

    /**
    清除查询条件
    **/
    this.clearSearcher = function () {
        this.searchForm.form('clear');
    };

    /**
    *导出
    *@type 导出文件类型
    **/
    this.export = function (type) {
        var name = $('title').val() + '导出';
        exportExcelBaseInfo('grid', 'do_export.htm', name);
    };

    /**
    *删除完成
    *@result 服务器端返回结果
    **/
    this.deleted = function (result) {
        if (result) {
            showSuc('删除成功!');
            this.grid.datagrid('reload');
        }
        else {
            showError('删除失败!');
        }
    };

    /**
    *删除失败
    **/
    this.deleteFail = function (error) {
        showError('删除失败!');
    }

    /**
    *删除记录
    **/
    this.del = function () {
        var self = this;
        var datas = self.grid.datagrid("getChecked");// 获取所有勾选checkbox的行
        if (datas.length < 1) {
            showError('请选择要删除的记录!');
            return;
        }
        $.messager.confirm("确认", "你确定要删除这" + datas.length + "条数据?", function (r) {
            if (!r)
                return;
            var ids = '';

            $.each(datas, function (i, item) {
                ids = ids + (i == 0 ? "" : ",") + item[self.premaryField];
            });
            var url = 'batchdelete';
            $.post(url, { ids: ids }).success(function (result, state, xhr) {
                self.deleted(result);
            }).error(function () {
                self.deleteFail();
            })
        });
    };

    /**
    *查询
    **/
    this.search = function () {
        this.loadGridData();
    };

};

//dataFilter
$(function ($) {
    var cache = {};
    function getData(field, options) {

        var category = options.fields[field];

        if (!category)
            return;
        var data = cache[category];
        if (data != null)
            return data;

        var service = mdm.service.categoryService;
        data = options.data[category];
        if (data == null) {
            data = options.data[category] = service.getItems(category);
        } else if (typeof data == 'function') {
            var fun = data;
            data = fun.apply(this, [category]);
        }
        cache[category] = data;

        return data;
    }

    function find(data, val, options) {
        var txt = '';
        if (data instanceof Array) {
            $.each(data, function (i, n) {
                if (n[options.valueField] == val)
                    txt = n[options.textField];
            });
        } else {
            return data[val];
        }
        return txt;
    }

    var filter = function (data) {
        var rows = data.rows;
        var options = $.data(this, 'dataFilter').options;
        if (rows == null)
            rows = data;

        $.each(data.rows, function (i, row) {
            $.each(row, function (p, val) {
                var data = getData(p, options);
                if (data) {
                    row[p + options.suffix] = find(data, row[p], options);
                }
            });
        });
        cache = {};
        return data;
    };

    function bind(target) {
        var options = $.data(target, 'dataFilter').options;
        var grid = $(target);
        try {
            grid.datagrid({
                loadFilter: filter
            });
        } catch (e) {

        }
    }

    $.fn.dataFilter = function (options, params) {
        options = options || {};
        return this.each(function () {
            if (!$.data(this, 'dataFilter')) {
                $.data(this, 'dataFilter', {
                    options: $.extend({}, $.fn.dataFilter.defaults, options)
                });
            }
            bind(this);
        });
    };

    $.fn.dataFilter.defaults = {
        suffix: "Str",
        textField: "name",
        valueField: "code",
        fields: {},
        data: {}
    };

});

//数据字典组件
$(function ($) {
    var proxies = [];

    function Proxy(target) {
        var self = this;
        this.target = $(target);

        function init() {
            var names = $.fn.category.enum;
            self.target.data("proxy", this);
            var attr = self.target.attr('category');
            if (!attr)
                return;
            self.categoryId = names[attr];

            attr = self.target.attr('parent-category');
            if (attr)
                self.parentCategoryId = names[attr];

            try {
                var opt = this.target.combobox('options');
                if (opt && opt.onChange) {
                    this._onChange = opt.onChange;
                }
            } catch (e) {

            }

            self.target.combobox({
                valueField: 'code',
                textField: 'name',
                editable: false,
                onChange: onChanged
                // formatter: function (row) {
                // return row['entryCode'] + "→" + row['entryName'];
                // }
            });

            if (!self.parentCategoryId)
                self.load();
        }

        this.load = function () {
            loadData();
        };

        this.getValue = function () {
            return self.target.combobox('getValue');
        };

        this.reLoad = function (parent) {
            self.target.combobox('clear').trigger('onselected');
            loadData(parent.getValue());
        };

        function onChanged() {
            var target = self.target;
            target.trigger('onChanged');
            if (self._onChange)
                self._onChange.call(target, arguments[0], arguments[1]);
        }

        function getUrl(data) {
            var url = $.fn.category.url + 'get_biz?lookupId=' + self.categoryId;
            if (self.parentCategoryId) {
                url = $.fn.category.url + 'get_child_entry_bycode?lookupId='
						+ self.parentCategoryId + '&subLookupId='
						+ self.categoryId + "&code=" + data;
            }
            return url;
        }

        function loadData(data) {
            var url = getUrl(data);
            self.target.combobox("reload", url);
        }

        init.call(this);
    }

    Proxy.prototype.bind = function (event, handle) {
        this.target.bind(event, handle);
    };

    var selectedEventHandle = function (event) {
        var target = event.target;
        var proxy = $.data(target, "proxy");
        $.each(proxies, function (index, item) {
            if (item.parent && item.parent == proxy)
                item.reLoad(proxy);
        });
    };

    function findParent(index, parentCategoryId) {
        for (var i = index; i >= 0; i--) {
            var parent = proxies[i];
            if (parent.categoryId == parentCategoryId)
                return parent;
        }
        return null;
    }

    function bindElement(target) {
        var comboxes = null;
        if (target == null)
            comboxes = $(".easyui-combobox");
        else
            comboxes = $(target).find(".easyui-combobox");

        for (var i = 0; i < comboxes.length; i++) {
            var target = comboxes[i];
            var proxy = new Proxy(target);
            proxy.index = i;
            proxy.bind("onChanged", selectedEventHandle);
            proxies.push(proxy);
            if (proxy.parentCategoryId)
                proxy.parent = findParent(i, proxy.parentCategoryId);
        }
        ;
    }

    $.fn.category = function (options, params) {
        var options = options || {};
        return this.each(function () {
            bindElement(this);
        });
    };

    $.fn.category.url = BasePath + '/lookup_entry/';
    $.fn.category.enum = {
        PROVINCE: 1, // 省
        CITY: 2, // 市
        COUNTY: 3, // 县
        STATUS: 4, // 状态
        YEAR: 5, // 年份
        SEASON: 6, // 季节
        GENDER: 7, // 性别
        COUNTRY: 8, // 国家
        AREA: 9, // 区
        BRAND: 10, // 品牌
        AREA_LEVEL: 11, // 区域级别
        SHOP_TYPE: 12, // 店铺类型
        SHOP_LEVEL: 13, // 店铺级别
        WHOLESALE_TYPE: 14, // 批发类型
        TYPES_RETAIL: 15, // 零售类型
        MAIN_CATEGORY: 16, // 主营品类
        CUSTOMER_TYPE: 17, // 客户类型
        CUSTOMER_PROPERTY: 18, // 客户性质
        CUSTOMER_LEVEL: 19, // 客户等级
        MALL_TYPE: 20, // 商场类型
        MALL_LEVEL: 21, // 商场级别
        GOODS_STATUS: 23, // 商品状态
        MODEL: 24, // 款型
        MAIN_INGREDIENT: 25, // 主料
        FABRIC: 26, // 面料
        LINING: 27, // 内里
        SHAPE: 28, // 楦型
        ORDER_STYLE: 29, // 订货形式
        BUSINESS_NATURE: 30, // 供应商经营性质
        SUPPLIER_TYPE: 31, // 供应商类别
        OPENING_BANK: 32, // 开户行
        TAX_LEVEL: 33, // 纳税级别
        HEEL_TYPE: 34, // 跟型
        BOTTOM_TYPE: 35, // 底型
        BILL_STATUS: 36, // 单据状态
        TAX_RATE: 38, // 税率
        ORGAN_LEVEL: 39, // 机构级别
        CLEARING_FORM: 42, // 结算方式
        COUNTER_STYLE: 43, // 柜台形式
        MONTH: 44, // 月份
        MARKETING_CHANNEL_A: 46, // 一级销售渠道
        MARKETING_CHANNEL_C: 47, // 二级销售渠道
        MARKETING_CHANNEL_D: 48, // 三级销售渠道
        SHOP_CATEGORY_A: 49, // 店铺大类
        SHOP_CATEGORY_B: 50, // 店铺小类
        SHOP_CATEGORY_C: 51, // 店铺细类
        SHOP_STATUS: 52, //店铺状态
        BRAND_CATEGORY: 53, // 品牌类别
        BRAND_BELONG: 54, // 品牌归属
        SHOP_PAY_TYPE: 55, // 店铺结算方式
        SHOP_DIGITS: 56, // 店铺收银位数
        SHOP_MAJOR: 57, // 店铺主营品类
        SHOP_MULTI: 58, // 店铺单品多品分类
        ITEM_LEVEL: 59, // 货品等级
        BILL_TYPE: 60, // 单据类型
        BILL_ACCFLAG: 61, // 单据执行标志
        ITEM_MEASURE: 62,  //计量单位
        ITEM_STYLE: 63,  //货品风格
        SALE_MODE: 64, //批发，零售
        STORAGE_TYPE: 65 , //仓库类别
        PRICE_ADJUST_LEVEL: 69 //调价级别
    };

    bindElement(null);

    $.fn.combobox.methods.getSelectedData = function (jq, param) {
        var valueField = $(jq).data.combobox.options.valueField;
        var data = $(jq).combobox('getData');
        var val = $(jq).combobox('getValue');
        var result = null;
        if (!val)
            return result;
        $.each(data, function (i, row) {
            if (row[valueField] == val)
                resul = row;
        })
        return result;
    }
});

// 替换原有easyui 的form的扩展方法，原load和reset方法存在bug
(function ($) {
    /**
	 * submit the form
	 */
    function ajaxSubmit(target, options) {
        options = options || {};

        var param = {};
        if (options.onSubmit) {
            if (options.onSubmit.call(target, param) == false) {
                return;
            }
        }

        var form = $(target);
        if (options.url) {
            form.attr('action', options.url);
        }
        var frameId = 'easyui_frame_' + (new Date().getTime());
        var frame = $(
				'<iframe id=' + frameId + ' name=' + frameId + '></iframe>')
				.attr(
						'src',
						window.ActiveXObject ? 'javascript:false'
								: 'about:blank').css({
								    position: 'absolute',
								    top: -1000,
								    left: -1000
								});
        var t = form.attr('target'), a = form.attr('action');
        form.attr('target', frameId);

        var paramFields = $();
        try {
            frame.appendTo('body');
            frame.bind('load', cb);
            for (var n in param) {
                var f = $('<input type="hidden" name="' + n + '">').val(
						param[n]).appendTo(form);
                paramFields = paramFields.add(f);
            }
            checkState();
            form[0].submit();
        } finally {
            form.attr('action', a);
            t ? form.attr('target', t) : form.removeAttr('target');
            paramFields.remove();
        }

        function checkState() {
            var f = $('#' + frameId);
            if (!f.length) {
                return
            }
            try {
                var s = f.contents()[0].readyState;
                if (s && s.toLowerCase() == 'uninitialized') {
                    setTimeout(checkState, 100);
                }
            } catch (e) {
                cb();
            }
        }

        var checkCount = 10;
        function cb() {
            var frame = $('#' + frameId);
            if (!frame.length) {
                return
            }
            frame.unbind();
            var data = '';
            try {
                var body = frame.contents().find('body');
                data = body.html();
                if (data == '') {
                    if (--checkCount) {
                        setTimeout(cb, 100);
                        return;
                    }
                    // return;
                }
                var ta = body.find('>textarea');
                if (ta.length) {
                    data = ta.val();
                } else {
                    var pre = body.find('>pre');
                    if (pre.length) {
                        data = pre.html();
                    }
                }
            } catch (e) {

            }
            if (options.success) {
                options.success(data);
            }
            setTimeout(function () {
                frame.unbind();
                frame.remove();
            }, 100);
        }
    }

    function post(target, options) {
        options = options || {};

        var param = {};

        var form = $(target);
        var url = options.url;
        var data1 = {};
        $.each($('input,textarea,select', form), function (i, input) {
            var name = $(input).attr('name');
            if (name)
                data1[name] = $(input).val();
        });
        var data2 = _getCombo(target);
        param = $.extend(param, data1, data2);
        if (options.onSubmit) {
            param = options.onSubmit.call(target, param);
        }
        var def = $.Deferred();
        $.post(url, param).success(function (data, state, xhr) {
            def.resolve(data);
        }).error(function () {
            def.reject();
        });
        return def;
    }

    function _getCombo(target) {
        var form = $(target);
        var cc = $.fn.form.plugins;
        var data = {};
        for (var i = 0; i < cc.length; i++) {
            var type = cc[i];
            var cmbs = form.find("." + type + '-f');
            $.each(cmbs, function (i, e) {
                var c = $(e);
                var name = c.attr('comboName');
                var val = null;
                if (c[type]('options').multiple) {
                    val = c[type]('getValues');
                } else {
                    val = c[type]('getValue');
                }
                data[name] = val;
            });
        }
        return data;

    }
    /**
	 * load form data if data is a URL string type load from remote site,
	 * otherwise load from local data object.
	 */
    function load(target, data) {
        if (!$.data(target, 'form')) {
            $.data(target, 'form', {
                options: $.extend({}, $.fn.form.defaults)
            });
        }
        var opts = $.data(target, 'form').options;

        if (typeof data == 'string' && data !== "") {
            var param = {};
            if (opts.onBeforeLoad.call(target, param) == false)
                return;
            var def = $.Deferred();
            $.ajax({
                url: data,
                data: param,
                dataType: 'json',
                success: function (data) {
                    _load(data);
                    def.resolve(data);
                },
                error: function () {
                    opts.onLoadError.apply(target, arguments);
                    def.reject();
                }
            });
            return def;
        } else {
            _load(data);
        }

        function _load(data) {
            var form = $(target);
            for (var name in data) {
                var val = data[name];
                var rr = _checkField(name, val);

                if (!_loadCombo(name, val))// 方法提前，防止直接load input
                    // element造成combo不触发onchange事件
                {
                    if (!rr.length) {

                        var count = _loadOther(name, val);
                        if (!count) {
                            $('input[name="' + name + '"]', form).val(val);
                            $('textarea[name="' + name + '"]', form).val(val);
                            $('select[name="' + name + '"]', form).val(val);
                        }
                    }
                }

            }
            opts.onLoadSuccess.call(target, data);
            validate(target);
        }

        /**
		 * check the checkbox and radio fields
		 */
        function _checkField(name, val) {
            var rr = $(target).find(
					'input[name="' + name + '"][type=radio], input[name="'
							+ name + '"][type=checkbox]');
            rr._propAttr('checked', false);
            rr
					.each(function () {
					    var f = $(this);
					    if (f.val() == String(val)
								|| $.inArray(f.val(), $.isArray(val) ? val
										: [val]) >= 0) {
					        f._propAttr('checked', true);
					    }
					});
            return rr;
        }

        function _loadOther(name, val) {
            var count = 0;
            var pp = ['numberbox', 'slider'];
            for (var i = 0; i < pp.length; i++) {
                var p = pp[i];
                var f = $(target).find('input[' + p + 'Name="' + name + '"]');
                if (f.length) {
                    f[p]('setValue', val);
                    count += f.length;
                }
            }
            return count;
        }

        function _loadCombo(name, val) {
            var form = $(target);
            var cc = $.fn.form.plugins;
            var c = form.find('[comboName="' + name + '"]');
            if (c.length) {
                for (var i = 0; i < cc.length; i++) {
                    var type = cc[i];
                    if (c.hasClass(type + '-f')) {
                        if (c[type]('options').multiple) {
                            c[type]('setValues', val);
                        } else {
                            c[type]('setValue', val);
                        }
                        return true;
                    }
                }
            }
        }
    }

    /**
	 * clear the form fields
	 */
    function clear(target) {

        /** 将该段代码执行提前，防止直接clear input element造成combo不触发onchange事件 * */
        var t = $(target);
        var plugins = ['combo', 'combobox', 'combotree', 'combogrid', 'slider', 'managercity', 'bizCity'];
        for (var i = 0; i < plugins.length; i++) {
            var plugin = plugins[i];
            var r = t.find('.' + plugin + '-f');
            if (r.length && r[plugin]) {
                r[plugin]('clear');
            }
        }
        //validate(target);错误的触发combobox层弹出
        /** -------------------------------end ----------------------* */

        $('input,select,textarea', target).each(
				function () {
				    var t = this.type, tag = this.tagName.toLowerCase();
				    if (t == 'text' || t == 'hidden' || t == 'password'
							|| tag == 'textarea') {
				        this.value = '';
				    } else if (t == 'file') {
				        var file = $(this);
				        var newfile = file.clone().val('');
				        newfile.insertAfter(file);
				        if (file.data('validatebox')) {
				            file.validatebox('destroy');
				            newfile.validatebox();
				        } else {
				            file.remove();
				        }
				    } else if (t == 'checkbox' || t == 'radio') {
				        this.checked = false;
				    } else if (tag == 'select') {
				        this.selectedIndex = -1;
				    }

				});

    }

    function reset(target) {
        target.reset();
        var t = $(target);
        var plugins = ['combo', 'combobox', 'combotree', 'combogrid',
                       'datebox', 'datetimebox', 'spinner', 'timespinner',
                       'numberbox', 'numberspinner', 'slider', 'managercity', 'bizCity'];
        for (var i = 0; i < plugins.length; i++) {
            var plugin = plugins[i];
            var r = t.find('.' + plugin + '-f');
            if (r.length && r[plugin]) {
                r[plugin]('reset');
            }
        }
        validate(target);
    }

    /**
	 * set the form to make it can submit with ajax.
	 */
    function setForm(target) {
        var options = $.data(target, 'form').options;
        var form = $(target);
        form.unbind('.form').bind('submit.form', function () {
            setTimeout(function () {
                ajaxSubmit(target, options);
            }, 0);
            return false;
        });
    }
    function validate(target) {
        if ($.fn.validatebox) {
            var t = $(target);
            t.find('.validatebox-text:not(:disabled)').validatebox('validate');
            var invalidbox = t.find('.validatebox-invalid');
            invalidbox.filter(':not(:disabled):first').focus();
            return invalidbox.length == 0;
        }
        return true;
    }

    function setValidation(target, novalidate) {
        $(target).find('.validatebox-text:not(:disabled)').validatebox(
				novalidate ? 'disableValidation' : 'enableValidation');
    }

    /*
	 * $.fn.form = function (options, param) { if (typeof options == 'string') {
	 * return $.fn.form.methods[options](this, param); }
	 * 
	 * options = options || {}; return this.each(function () { if (!$.data(this,
	 * 'form')) { $.data(this, 'form', { options: $.extend({},
	 * $.fn.form.defaults, options) }); } setForm(this); }); };
	 */
    $.fn.form.methods.load = function (jq, data) {
        return load(jq[0], data);
    };

    $.fn.form.methods.getData = function (jq, data) {
        var data = jq.serializeArray();
        var result = {};
        $.each(data, function (i, r) {
            result[r.name] = r.value;
        });
        return result;
    };

    $.fn.form.methods.clear = function (jq) {
        return jq.each(function () {
            clear(this);
        });
    };

    $.fn.form.methods.reset = function (jq) {
        return jq.each(function () {
            reset(this);
        });
    };

    $.fn.form.methods.post = function (jq, param) {
        return post(jq[0], param);
    }

    $.fn.form.plugins = ['combobox', 'combotree', 'combogrid', 'datetimebox',
                'datebox', 'combo'];
})(jQuery);

//panel
$(function ($) {

    function showPanel(target, options) {
        var dialog = null;
        options = $.extend({}, $.fn.showpanel.defaults, {
            target: $(target),
            buttons: [{
                text: '保存',
                iconCls: 'icon-save',
                handler: function (dialog) {
                    options.ok(dialog);
                }
            }, {
                text: '取消',
                iconCls: 'icon-cancel',
                handler: function (dialog) {
                    options.cancel(dialog);
                }
            }]
        }, options);
        dialog = ygDialog(options);
        return dialog;
    };

    $.fn.showpanel = function (options, param) {
        if (typeof options == 'string') {
            return;
        }

        showPanel(this[0], options);
    };

    $.fn.showpanel.defaults = {
        title: '新增',
        width: 580,
        height: 300,
        ok: function (dialog) { dialog.close(); },
        cancel: function (dialog) { dialog.close(); }
    };

    $.fn.showpanel.methods = {

    };

});

/**业务实体查询相关UI组件 **/
//Brand
(function ($) {
    $.parser.plugins.push('brand');
    $.fn.form.plugins.push('brand');
    function bind(target) {
        var options = $.data(target, 'brand').options;

        $.ajax({
            url: options.url,
            cache: true
        }).then(function (data) {
            var values = $.map(data, function (item) {
                return { id: item[options.valueField], text: item[options.textField] };
            });
            $(target).combotree('loadData', values);
        });
        var op = $.extend({}, options);
        delete op.valueField;
        delete op.textField;
        delete op.url;
        $(target).combotree(op);
    }

    $.fn.brand = function (options, param) {
        if (typeof options == 'string') {
            return $.fn.brand.methods[options](this, param);
        }

        options = options || {};
        return this.each(function () {
            var state = $.data(this, 'brand');
            if (state) {
                options = $.extend({}, $.fn.brand.defaults, options)
            } else {
                var op = $.fn.brand.parseOptions(this);
                options = $.extend({}, $.fn.brand.defaults, options, op);
                $.data(this, 'brand', { options: options });
            }
            bind(this);
        });
    };
    $.fn.brand.parseOptions = function (target) {
        return $.parser.parseOptions(target, [
			'valueField', 'textField', 'url'
        ]);
    }

    $.fn.brand.defaults = {
        width: 160,
        url: '../brand/get_biz?status=1',
        panelHeight: "auto",
        multiple: true,
        textField: 'name',
        valueField: 'brandNo'
    };

    $.fn.brand.methods = {
        options: function (jq) {
            return $.data(jq[0], 'brand').options;
        },
        getValues: function (jq) {
            return $(jq[0]).combotree('getValues');
        },
        setValues: function (jq, param) {
            $(jq[0]).combotree('setValues', param);
        }
    }

})(jQuery);

//managercity
(function ($) {
    var _name = 'managercity';
    $.parser.plugins.push(_name);
    $.fn.form.plugins.push(_name);

    function bind(target) {
        var options = $.data(target, _name).options;
        var op = $.extend({}, options);
        op.href = options.url + '/listSelect';
        var name = $(target).attr('name');
        var hd = $('<input type="hidden" class="combo-value"/>').appendTo($(target).parent());
        op.fn = function (data) {
            $.data(target, _name).data = data;
            $(target).val(data[options.textField]);
            hd.val(data[options.valueField]);
        };

        hd.attr('name', name);
        $(target).removeAttr('name');
        $(target).addClass('ipt');
        $(target).addClass('managercity-f');
        $(target).attr('editable', "false");
        $(target).attr('comboName', name);
        $(target).iptSearch({
            clickFn: function () {
                dgSelector(op);
            }
        });
    }

    function _setValue(target, param) {
        var options = $.data(target, _name).options;
        if( param ){
            var url = options.url + '/get_biz?' + options.valueField + "=" + param;
            $.ajax(url).then(function (result) {
                if (!result && result.length > 0)
                    return;
                $.data(target, _name).data = result[0];
                $(target).val(result[0][options.textField]);
                $(target).next('.combo-value').val(param);
            });
        }else{
            $.data(target, _name).data =null;
            $(target).val('');
            $(target).next('.combo-value').val(null);
        }
    }

    $.fn[_name] = function (options, param) {
        if (typeof options == 'string') {
            return $.fn[_name].methods[options](this, param);
        }
        options = options || {};
        return this.each(function () {
            var state = $.data(this, _name);
            if (state) {
                $.extend(state.options, options);
            } else {
                var op = $.fn[_name].parseOptions(this);
                options = $.extend({}, $.fn[_name].defaults, options, op);
                $.data(this, _name, { options: options });
            }
            bind(this);
        });
    };

    $.fn[_name].parseOptions = function (target) {
        return $.parser.parseOptions(target, [
			'valueField', 'textField', 'url'
        ]);
    }

    $.fn[_name].defaults = {
        width: 500,
        height: 500,
        title: '选择管理城市',
        url: '../organ',
        multiple: false,
        textField: 'name',
        valueField: 'organNo'
    };

    $.fn[_name].methods = {
        options: function (jq) {
            return $.data(jq[0], _name).options;
        },
        getValue: function (jq) {
            var target = jq[0];
            var state = $.data(target, _name);
            if (!state || !state.data)
                return null;
            var data = state.data;            
            return data[state.options.valueField];
        },
        setValue: function (jq, param) {
            $.each(jq, function (i, j) {
                _setValue(j, param);
            })
        },
        clear:function(jq){
            $.each(jq, function (i, j) {
                _setValue(j, null);
            })
        },
        reset:function(jq){
            $.each(jq, function (i, j) {
                _setValue(j, null);
            })
        }
    }

})(jQuery);

//结算公司
(function ($) {
    var _name = 'company';
    $.parser.plugins.push(_name);
    //$.fn.form.plugins.push(_name);

    function bind(target) {
        var options = $.data(target, _name).options;

        //$.ajax({
        //    url: options.url,
        //    cache: false
        //}).then(function (data) {
  
        //    $(target).combobox('loadData', data);
        //});
        var op = $.extend({}, options);
        //delete op.url;
        $(target).combobox(op);
    }

    $.fn.company = function (options, param) {
        if (typeof options == 'string') {
            return $.fn.company.methods[options](this, param);
        }

        options = options || {};
        return this.each(function () {
            var state = $.data(this, _name);
            if (state) {
                $.extend(state.options, options);
            } else {
                var op = $.fn[_name].parseOptions(this);
                options = $.extend({}, $.fn[_name].defaults, options, op);
                $.data(this, _name, { options: options });
            }
            bind(this);
        });
    };
    $.fn.company.parseOptions = function (target) {
        return $.parser.parseOptions(target, [
			'valueField', 'textField', 'url'
        ]);
    }

    $.fn.company.defaults = {
        width: 160,
        url: '../company/get_biz?status=1',
        panelHeight: "auto",
        textField: 'name',
        valueField: 'id'
    };

    $.fn.company.methods = {
        options: function (jq) {
            return $.data(jq[0], _name).options;
        },
        getText:function(jq){
            return $(jq[0]).combobox('getSelectedData')['name'];
        },
        getItem: function (jq) {
            return $(jq[0]).combobox('getSelectedData');
        }
    }
})(jQuery);

//Zone_Info
(function ($) {
    $.parser.plugins.push('zoneinfo');
    $.fn.form.plugins.push('zoneinfo');
    function bind(target) {
        var options = $.data(target, 'zone_info').options;

        $.ajax({
            url: options.url,
            cache: true
        }).then(function (data) {
            var values = $.map(data, function (item) {
                return { id: item[options.valueField], text: item[options.textField] };
            });
            $(target).combotree('loadData', values);
        });
        var op = $.extend({}, options);
        delete op.valueField;
        delete op.textField;
        delete op.url;
        $(target).combotree(op);
    }

    $.fn.zoneinfo = function (options, param) {
        if (typeof options == 'string') {
            return $.fn.zoneinfo.methods[options](this, param);
        }

        options = options || {};
        return this.each(function () {
            var state = $.data(this, 'zone_info');
            if (state) {
                options = $.extend({}, $.fn.zoneinfo.defaults, options)
            } else {
                var op = $.fn.zoneinfo.parseOptions(this);
                options = $.extend({}, $.fn.zoneinfo.defaults, options, op);
                $.data(this, 'zone_info', { options: options });
            }
            bind(this);
        });
    };
    $.fn.zoneinfo.parseOptions = function (target) {
        return $.parser.parseOptions(target, [
			'valueField', 'textField', 'url'
        ]);
    }

    $.fn.zoneinfo.defaults = {
        width: 160,
        url: '../zone_info/get_biz?status=1',
        panelHeight: "auto",
        multiple: true,
        textField: 'name',
        valueField: 'zoneNo'
    };

    $.fn.zoneinfo.methods = {
        options: function (jq) {
            return $.data(jq[0], 'zoneinfo').options;
        },
        getValues: function (jq) {
            return $(jq[0]).combotree('getValues');
        },
        setValues: function (jq, param) {
            $(jq[0]).combotree('setValues', param);
        }
    };

})(jQuery);

//大区的下拉框
(function ($) {
    var _name = 'zoneInfoBox';
    $.parser.plugins.push(_name);
    //$.fn.form.plugins.push(_name);

    function bind(target) {
        var options = $.data(target, _name).options;
        var op = $.extend({}, options);
        $(target).combobox(op);
    }

    $.fn.zoneInfoBox = function (options, param) {
        if (typeof options == 'string') {
            return $.fn.zoneInfoBox.methods[options](this, param);
        }

        options = options || {};
        return this.each(function () {
            var state = $.data(this, _name);
            if (state) {
                $.extend(state.options, options);
            } else {
                var op = $.fn[_name].parseOptions(this);
                options = $.extend({}, $.fn[_name].defaults, options, op);
                $.data(this, _name, { options: options });
            }
            bind(this);
        });
    };
    $.fn.zoneInfoBox.parseOptions = function (target) {
        return $.parser.parseOptions(target, [
			'valueField', 'textField', 'url'
        ]);
    }

    $.fn.zoneInfoBox.defaults = {
        width: 160,
        url: '../zone_info/get_biz?status=1',
        panelHeight: "auto",
        textField: 'name',
        valueField: 'zoneNo'
    };

    $.fn.zoneInfoBox.methods = {
        options: function (jq) {
            return $.data(jq[0], _name).options;
        },
        getText:function(jq){
            return $(jq[0]).combobox('getSelectedData')['name'];
        },
        getItem: function (jq) {
            return $(jq[0]).combobox('getSelectedData');
        }
    };
})(jQuery);

//经营城市弹出精灵
(function ($) {
    var _name = 'bizCity';
    $.parser.plugins.push(_name);
    $.fn.form.plugins.push(_name);

    function bind(target) {
        var options = $.data(target, _name).options;
        var op = $.extend({}, options);
        op.href = options.url + '/listSelect';
        var name = $(target).attr('name');
        var hd = $('<input type="hidden" class="combo-value"/>').appendTo($(target).parent());
        op.fn = function (data) {
            $.data(target, _name).data = data;
            $(target).val(data[options.textField]);
            hd.val(data[options.valueField]);
        };

        hd.attr('name', name);
        $(target).removeAttr('name');
        $(target).addClass('ipt');
        $(target).addClass('bizCity-f');
        $(target).attr('editable', "false");
        $(target).attr('comboName', name);
        $(target).iptSearch({
            clickFn: function () {
                dgSelector(op);
            }
        });
    }

    function _setValue(target, param) {
        var options = $.data(target, _name).options;
        if( param ){
            var url = options.url + '/get_biz?' + options.valueField + "=" + param;
            $.ajax(url).then(function (result) {
                if (!result && result.length > 0)
                    return;
                $.data(target, _name).data = result[0];
                $(target).val(result[0][options.textField]);
                $(target).next('.combo-value').val(param);
            });
        }else{
            $.data(target, _name).data =null;
            $(target).val('');
            $(target).next('.combo-value').val(null);
        }
    }

    $.fn[_name] = function (options, param) {
        if (typeof options == 'string') {
            return $.fn[_name].methods[options](this, param);
        }
        options = options || {};
        return this.each(function () {
            var state = $.data(this, _name);
            if (state) {
                $.extend(state.options, options);
            } else {
                var op = $.fn[_name].parseOptions(this);
                options = $.extend({}, $.fn[_name].defaults, options, op);
                $.data(this, _name, { options: options });
            }
            bind(this);
        });
    };

    $.fn[_name].parseOptions = function (target) {
        return $.parser.parseOptions(target, [
			'valueField', 'textField', 'url'
        ]);
    }

    $.fn[_name].defaults = {
        width: 500,
        height: 500,
        title: '选择经营城市',
        url: '../biz_city',
        multiple: false,
        textField: 'name',
        valueField: 'bizCityNo'
    };

    $.fn[_name].methods = {
        options: function (jq) {
            return $.data(jq[0], _name).options;
        },
        getValue: function (jq) {
            var target = jq[0];
            var state = $.data(target, _name);
            if (!state || !state.data)
                return null;
            var data = state.data;            
            return data[state.options.valueField];
        },
        setValue: function (jq, param) {
            $.each(jq, function (i, j) {
                _setValue(j, param);
            })
        },
        clear:function(jq){
            $.each(jq, function (i, j) {
                _setValue(j, null);
            })
        },
        reset:function(jq){
            $.each(jq, function (i, j) {
                _setValue(j, null);
            })
        }
    }

})(jQuery);

//供应商的下拉框
(function ($) {
    var _name = 'supplierbox';
    $.parser.plugins.push(_name);
    //$.fn.form.plugins.push(_name);

    function bind(target) {
        var options = $.data(target, _name).options;
        var op = $.extend({}, options);
        $(target).combobox(op);
    }

    $.fn.supplierbox = function (options, param) {
        if (typeof options == 'string') {
            return $.fn.supplierbox.methods[options](this, param);
        }

        options = options || {};
        return this.each(function () {
            var state = $.data(this, _name);
            if (state) {
                $.extend(state.options, options);
            } else {
                var op = $.fn[_name].parseOptions(this);
                options = $.extend({}, $.fn[_name].defaults, options, op);
                $.data(this, _name, { options: options });
            }
            bind(this);
        });
    };
    $.fn.supplierbox.parseOptions = function (target) {
        return $.parser.parseOptions(target, [
			'valueField', 'textField', 'url'
        ]);
    };

    $.fn.supplierbox.defaults = {
        width: 160,
        url: '../supplier/get_biz?status=1',
        panelHeight: 200,
        textField: 'shortName',
        valueField: 'supplierNo'
    };

    $.fn.supplierbox.methods = {
        options: function (jq) {
            return $.data(jq[0], _name).options;
        },
        getText:function(jq){
            return $(jq[0]).combobox('getSelectedData')['name'];
        },
        getItem: function (jq) {
            return $(jq[0]).combobox('getSelectedData');
        }
    };
})(jQuery);


//管理城市的下拉框
(function ($) {
    var _name = 'managecitybox';
    $.parser.plugins.push(_name);
    //$.fn.form.plugins.push(_name);

    function bind(target) {
        var options = $.data(target, _name).options;
        var op = $.extend({}, options);
        $(target).combobox(op);
    }

    $.fn.managecitybox = function (options, param) {
        if (typeof options == 'string') {
            return $.fn.managecitybox.methods[options](this, param);
        }

        options = options || {};
        return this.each(function () {
            var state = $.data(this, _name);
            if (state) {
                $.extend(state.options, options);
            } else {
                var op = $.fn[_name].parseOptions(this);
                options = $.extend({}, $.fn[_name].defaults, options, op);
                $.data(this, _name, { options: options });
            }
            bind(this);
        });
    };
    $.fn.managecitybox.parseOptions = function (target) {
        return $.parser.parseOptions(target, [
			'valueField', 'textField', 'url'
        ]);
    };

    $.fn.managecitybox.defaults = {
        width: 160,
        url: '../organ/get_biz?status=1',
        panelHeight: 200,
        textField: 'name',
        valueField: 'organNo'
    };

    $.fn.managecitybox.methods = {
        options: function (jq) {
            return $.data(jq[0], _name).options;
        },
        getText:function(jq){
            return $(jq[0]).combobox('getSelectedData')['name'];
        },
        getItem: function (jq) {
            return $(jq[0]).combobox('getSelectedData');
        }
    };
})(jQuery);
/**业务实体查询相关UI组件 **/