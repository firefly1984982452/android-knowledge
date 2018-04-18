$(function(){
	/**
	 * 
	 * @param {Object} opts 设置toast的相关参数
	 * msg : toast显示的内容(默认：'')
	 * time: toast显示的时间(默认：1000毫秒)
	 * left: toast跟左边的距离(默认：0)
	 * right:toast跟右边的距离(默认：0)
	 * top : toast跟上边的距离(默认：0)
	 */
	$.fn.toast = function(opts){  //opts是一个json数据
		//$.extend({}//放的默认的参数值，第二个参数是传进来的值)
		//{msg:'haha',top:100}
		var settings = $.extend({
				msg : '',
				time : 1000,
				margin:'auto',
				left : '0',
				right : '0',
				top :'0'
		},opts||{});
		$("#toast").remove();
		//创建一个div，添加对应的样式
		var toast = $("<div id='toast'>").css({
			position:'fixed',
			margin:settings.margin,
			right:settings.right+'px',
			bottom:settings.bottom+'px',
			left:settings.left+'px',
			top:settings.top+'px',
			maxWidth: "400px",
			textAlign:'center',
			height: "auto",
			border: "1px solid rgba(255,0,0,0.5)",
			borderRadius: "5px",
			color:"#fff",
			background: "rgba(255,0,0,0.5)",
			padding: "5px 10px",
			boxShadow:" 0px 0px 5px rgba(255,0,0,0.5)",
			display: "none"
		});
		toast.html(settings.msg);
		toast.appendTo($(this)).fadeIn(500).delay(settings.time).fadeOut(500);	
	}
	
	
	
	
	$.fn.timeBtn = function(opt){
		var set = $.extend({
			time:10,//默认时间为10秒
			onClick:function(){}//默认为空函数
		}, opt||{});
		//1.初始化的时候，就为这个元素添加了一个样式
		$(this).addClass("mybtn");
		
		
		var op = this;			//记录当前元素
		var oldTime = set.time;  //记录倒计时时间
		
		var isAbled = true;//是否可以点击，默认为true
		
		//2.给这个按钮添加了一个绑定的事件
		$(this).on('click',function(){
			if(isAbled){//如果可以点击
				isAbled = false; //设置成不可点击，当倒计时完成之后设置为true
				$(op).removeClass("mybtn").addClass("mybtn_disable");
				set.onClick(); //调用传进来的点击方法
				daojishi();	//倒计时
				function daojishi(){
					if(set.time<=0) {
						$(op).text("重新获取");
						$(op).attr("disabled",false);
						set.time =oldTime;// 时间还原
						isAbled = true;//设置成true
						$(op).removeClass("mybtn_disable").addClass("mybtn");
						return;
					}
					set.time--;
					$(op).text(set.time+"秒后重新获取");
					setTimeout(daojishi,1000)
				}
			}
			return false;
		})
		
	}
})
