function print(toPrview) {

	var LODOP = getLodop();
	
	var strHTML=document.getElementsByTagName("html")[0].innerHTML;
    LODOP.PRINT_INITA(10,20,810,610,"测试C-Lodop远程打印四步骤");
    LODOP.ADD_PRINT_TEXT(1,1,300,200,"下面输出的是本页源代码及其展现效果：");
    LODOP.ADD_PRINT_TEXT(20,10,"90%","95%",strHTML);
    LODOP.SET_PRINT_STYLEA(0,"ItemType",4);
    LODOP.NewPageA();
    LODOP.ADD_PRINT_HTM(20,10,"90%","95%",strHTML);
    if (toPrview == 0)
        LODOP.PREVIEW();
    else
        LODOP.PRINT();

	
}