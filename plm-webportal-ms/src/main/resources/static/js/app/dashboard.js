/*
file: dashboard.js
date: May 27, 2016
file purpose: set of functions for processing PO's with status "Error In Process"
server calls: none
*/

		var isFirstSearch='';
    	var lastPartition='';
    	var lastRow='';
    	var nextPartition='';
    	var nextRow='';
    	var startRowKey='';
    	var tableName='';
    	var endRowKey='';
    	var partition='';
    	var size='';
    	var obj2={};
/*
* Functions encapsulated in IIFE referenced by variable e2OpenProcessError
* @param {Object} jQuery 
*/
var e2OpenDashboard = (function($){
    var checkedRecords = [], mainGrid = {}, resultSetData={}, errorData = {}, erpData,allGridsData = {},graphData = {};
	var supplierData = {},itemData= {};
    
    /*
     * To create grid and graph on load (O records) and to adjust the graph height based on screen height
     */
    function init(){
        mainGrid = {};errorData = {};erpData={};allGridsData = {};graphData = {};
        var obj = JSON.parse('{"firstRequest":true,"size":100,"erpName":"SYMIX"}');
    	JSON.stringify(obj);
    	var obj2 = JSON.parse('{"paginationParam":{"lastPartition":null,"lastRow":null,"nextPartition":null,"nextRow":null}}');
    	JSON.stringify(obj2);
    	var finalObj = $.extend(obj, obj2);
    	var reqObj = JSON.stringify(finalObj);
        console.log("reqObj-->"+reqObj);
		
        serviceObj.pullPoData(reqObj).then(function( data, textStatus, jqXHR ) {
            if(!data.error){
				allGridsData = data;
				// remove the loading screen
				
				commonUtil.removeLoader();
				$('#username').text(data.userData.UserName);
				$('#username').attr('data-global-id',data.userData.GlobalId);
				$('.username-msg').show();
				$('.logout').show();
				
				errorData = data.errorData;
				graphData = data.graphData;
				
				fnGeneratePOSubMenus(data.resultSet);
				fnGenerateSupplierSubMenus(data.resultSet);
				fnGenerateItemSubMenus(data.resultSet);
				
				
				
				
				$('.nav-second-level li a').off('click').on('click',function(e){
					$('#main3').hide();	
					$('#main1').show();			
					e.stopPropagation();
					e.stopImmediatePropagation();
					var li = $(this).closest('ul').closest('li');
					li.siblings().find('.nav-second-level li a').each(function(){
						$(this).removeClass('active');
					});
					$(this).addClass('active');
					
					li.addClass('active');
					li.siblings().removeClass('active');
					
					var tab = $(this).closest('.nav-second-level').attr('data-item');
					var grid = $(this).attr('grid');
					var erp = grid.split('-')[1];
					if(tab == "po"){
						fnGenerateGridHtml(data.resultSet,grid,"dashboard",data.userData.Role,errorData);
						buildDashboardGrids(erp,data.resultSet);
						buildErrorGrids(errorData,erp);
					}						
					else if(tab == "supplier"){
						if(jQuery.isEmptyObject(supplierData.series)){
							var pagination = {};
							pagination.lastPartition = null;
							pagination.lastRow = null;
							pagination.nextPartition = null;
							pagination.nextRow = null;
							var obj = JSON.parse('{"firstRequest":true,"size":100,"erpName":"SYMIX"}');
					    	JSON.stringify(obj);
							var postObj = {} ;
							postObj.firstRequest = true; 
							postObj.paginationParam = pagination;
							postObj.erpName = erp;
							postObj.size = 10;
							var finalObj = $.extend(obj, postObj);
					    	//var reqObj = JSON.stringify(finalObj);
							commonUtil.addLoader();	
							serviceObj.callToSever("api/supplier/getSegmentedSupplierDetails",JSON.stringify(finalObj),"POST").then(function(result,xhr,status){
								commonUtil.removeLoader();
								supplierData = result;
								supplierGrid(supplierData,erp,grid);
							});
						}
						else
							supplierGrid(supplierData,erp,grid);
						
					}
						
					else if(tab == "item"){
						if(jQuery.isEmptyObject(itemData.series)){
							var pagination = {};
							pagination.lastPartition = null;
							pagination.lastRow = null;
							pagination.nextPartition = null;
							pagination.nextRow = null;
							var obj = JSON.parse('{"firstRequest":true,"size":100,"erpName":"SYMIX"}');
					    	JSON.stringify(obj);
							var postObj = {} ;
							postObj.firstRequest = true;  
							postObj.paginationParam = pagination;
							postObj.erpName = erp;
							postObj.size = 10;
							var finalObj = $.extend(obj, postObj);
							commonUtil.addLoader();
							serviceObj.callToSever("api/item/getSegmentedItemDetails",JSON.stringify(finalObj),"POST").then(function(result,xhr,status){
								commonUtil.removeLoader();
								itemData = result;
								itemGrid(itemData,erp,grid);
							});
						}
						else
							itemGrid(itemData,erp,grid);
						
					}
						
				});
				
				
				$('#side-menu li.menu').off('click').on('click',function(e){
					
					
						$(this).siblings().find('a').removeClass('active');
						$(this).find('a').not('.nav-second-level a').addClass('active');
						
						var li = $(this);
						li.addClass('active');
						li.siblings().removeClass('active');
						
						var data_tab = $(this).attr('data-tab');
						
						if(data_tab == "home"){
							/*fngenerateGraphHtml();
							var dashboardData = data.resultSet;
							erpData = commonUtil.dashboardErp(dashboardData);
							
							buildGraph(graphData,erpData);*/
							
							var menuHtml = "";
							menuHtml +='<li data-tab="home" class="menu active">'+
											'<a href="#" class="active"><i class="fa fa-home fa-fw" ></i> Home</a>'+
										'</li>';
										
							$('#side-menu').html(menuHtml);	
							$('#main1').html('');
							$('#main1').hide();
							$('#main2').hide();
							$('#main0').show();
							$('#main3').hide();
						}
						else if(data_tab == "po"){
							fnGeneratePOHtml();
							buildGraph(graphData);
						}
						else
							$(this).find('.nav-second-level li:first a').trigger('click');
					
				});
				
				$('li[data-tab="po"]').trigger('click');
				
            }
            console.log(erpData);
        });
    }
	
	$(document).on('click','#submitErrBtn',function(e){	
	//$('#submitErrBtn').off('click').on('click',function(){
		//var tab = $('#main2 .tab-pane.fade.in.active');
		
		var poNumArray = [];
		
		//var activeGrid = $('#main2 .tab-pane.fade.in.active .grid-style').attr('id');
		var activeGrid = ($('.nav.nav-second-level.collapse.in a.active').attr('grid')).replace('dashboard','error');
		
		var selectedRows = mainGrid[activeGrid].grid.getSelectedRows();
		
		if(selectedRows.length == 0){
			toastr.error('Select PO to be processed');
			return;
		}
		
		for(var i = 0 ; i < selectedRows.length ;i++){
			var item = mainGrid[activeGrid].grid.getDataItem(selectedRows[i])
			
			poNumArray.push(item.OrderNumber);
		}
		var comment = $('#txtDescErr').val();
		
		var erp = activeGrid.split('-')[1];	
		console.log(poNumArray);
		var userName = $('#username').text();
		var globalId = $('#username').attr('data-global-id');
		var sendObj = JSON.stringify({"erpName":erp,"poNo":poNumArray,"globalId": globalId,"userName": userName,"comment":comment});
		commonUtil.addLoader();
		serviceObj.callToSever("api/po/processErrorPos",sendObj,"POST").then(function(result,status,xhr){
				commonUtil.removeLoader();  
				console.log(result);  
				var successList = result.successList;
				var errorList = result.errorList;
				if(successList.length > 0 || errorList.length > 0){
					
					if(successList.length > 0){
						mainGrid[activeGrid].deleteItems(successList);
						$('#main1 .errro-data-container input[type="checkbox"]').each(function(){
							$(this).attr('checked',false);
						});
						var dashboardGrid = activeGrid.replace('error','dashboard');
						mainGrid[dashboardGrid].updateItems(successList);
						graphData = result.graphData;
						var newGraphData = {};
						newGraphData[erp] = graphData[erp];
						buildGraph(newGraphData);
						$('#txtDescErr').val('');
						
					}
					var sl = "",slMsg = "",el="",elMsg="";
					if(successList.length > 0){
						sl = successList.join(",");
						slMsg = "Processing Error Request success for "+sl;
					}
					if(errorList.length > 0){
						el = errorList.join(",")
						elMsg = "Error occured while processing process "+el;
					}
					
					if(slMsg)
						toastr.success(slMsg);
					
					if(elMsg)
						toastr.error(elMsg);
				}	
					
		});
	});
    
	$(document).on('click','#showDashboard',function(e){
		$('#main3').hide();
		$('#main1').show();
		$('#main3').removeClass('active');
		$('.nav.nav-second-level.collapse.in li').each(function(){
			$(this).removeClass('disabled');
		});
	});
	
	
	$(document).on('click','#exportBtn',function(e){
		var excelOptions = {
			  headerStyle: {
				  font: {
					  bold: true,  //enable bold
					  font: 12, // font size
					  color: 'ffffff' //font color --Note: Add 00 before the color code
				  },
				  fill: {   //fill background
					  type: 'pattern', 
					  patternType: 'solid',
					  fgColor: '428BCA' //background color --Note: Add 00 before the color code
				  }
			  },
			  cellStyle: {
				  font: {
					  bold: false,  //enable bold
					  font: 12, // font size
					  color: '000000' //font color --Note: Add 00 before the color code
				  },
				  fill: {   //fill background
					  type: 'pattern',
					  patternType: 'solid',
					  fgColor: 'ffffff' //background color --Note: Add 00 before the color code
				  }
			  },
		  };
  
		var activeGrid = $(this).attr('grid');
		
		var Data = mainGrid[activeGrid].dataView.getItems();
		
		 $('body').exportToExcel("Report.xlsx", "Report", Data, excelOptions, function (response) {
			console.log(response);
			window.open($('#downloadLink').attr('href'));
		});
	});
	
	//code to be run on load
	
	
    $(document).ready(function(){
		  
		var menuHtml = "";
		menuHtml +='<li data-tab="home" class="menu active">'+
                        '<a href="#" class="active"><i class="fa fa-home fa-fw" ></i> Home</a>'+
                    '</li>';
					
		$('#side-menu').html(menuHtml);	
		
		$('#scBtn').off('click').on('click',function(){
			$('#main1').removeClass('plm');
			$('#main1').addClass('sc');
			
			$('#page-wrapper').removeClass('plm');
			$('#page-wrapper').addClass('sc');
			menuHtml = "";
			menuHtml += '<li data-tab="home" class="menu">'+
                            '<a href="#"><i class="fa fa-home fa-fw"></i> Home</a>'+
                        '</li>'+
                        '<li data-tab="po" class="menu active">'+
                            '<a href="#"><i class="fa fa-dashboard fa-fw"></i> PO<span class="fa arrow"></span></a>'+
                            '<ul class="nav nav-second-level collapse in" id="PoSubMenu" data-item="po"></ul>'+
                        '</li>'+
						'<li data-tab="supplier" class="menu">'+
                            '<a href="#"><i class="fa fa-truck"></i> Supplier<span class="fa arrow"></span></a>'+
                            '<ul class="nav nav-second-level" id="SuppSubMenu" data-item="supplier"></ul>'+
                        '</li>'+
                        '<li data-tab="item" class="menu">'+
                            '<a href="#"><i class="fa fa-files-o fa-fw"></i> Item<span class="fa arrow"></span></a>'+
                            '<ul class="nav nav-second-level" id="ItemSubMenu" data-item="item"></ul>'+
                        '</li>';
						
			$('#side-menu').html(menuHtml);
			$('#side-menu').metisMenu();	
			$('#main0').hide();
			$('#main1').show();
			$("body").scrollTop(0);			
			init();
			commonUtil.addLoader();
		});	
        
        $('#plmBtn').off('click').on('click',function(){
			$('#main1').removeClass('sc');
			$('#main1').addClass('plm');
			
			$('#page-wrapper').removeClass('sc');
			$('#page-wrapper').addClass('plm');
			menuHtml = "";
			menuHtml += '<li data-tab="home" class="menu">'+
                            '<a href="#"><i class="fa fa-home fa-fw"></i> Home</a>'+
                        '</li>'+
                        '<li data-tab="ecn" class="menu active">'+
                            '<a href="#"><i class="fa fa-dashboard fa-fw"></i>ECN<span class="fa arrow"></span></a>'+
                            '<ul class="nav nav-second-level collapse in" id="ECNSubMenu" data-item="ecn"></ul>'+
                        '</li>';
						
			$('#side-menu').html(menuHtml);
			$('#side-menu').metisMenu();	
			$('#main0').hide();
			$('#main1').show();
			$("body").scrollTop(0);	
			commonUtil.addLoader();			
			EcnInit();
						
		});
    });	
    
    var lastPartition = "";
    var lastRowKey = "";
    
    function loadNextData(){
    	
    }

	function EcnInit(){
		var obj = JSON.parse('{"firstRequest":true,"size":1000,"erpName":"SYMIX"}');
    	JSON.stringify(obj);
    	var obj2 = JSON.parse('{"paginationParam":{"lastPartition":null,"lastRow":null,"nextPartition":null,"nextRow":null}}');
    	JSON.stringify(obj2);
    	var finalObj = $.extend(obj, obj2);
    	var reqObj = JSON.stringify(finalObj);
        console.log("reqObj-->"+reqObj);
		var type = "ecn";
		
			serviceObj.pullPoData(reqObj,type).then(function( data, textStatus, jqXHR ) {
			//data = {"message":"OK","resultSet":{"SYMIX":{"series":[{"PTCAckMsg":"PTCACkMessage","Description":"CUMMINS PART NPI","BomPayloadProcessed":"true","ModifiedDate":"2016-07-22T00:00:00Z","ProcessedDate":"2016-07-27T00:00:00Z","EcnRequestor":"Administrator","Plant":"Reynosa","UIReprocessingDate":"2016-07-27T00:00:00Z","PartError":"true","id":"0004","BomErrorMsg":"BomErrorMessage","ECNNumber":"115","PtcAck":"false","isErrorDataRequired":"false","Status":"3","OutputXMLEtag":"cdc3b234","PartPayloadProcessed":"false","ERPName":"Symix","Error":"401","BomError":"true","TxnID":"5","PartPayloadProcessedDate":"2016-07-22T00:00:00Z","InputXMLEtag":"cdc3b121","UIReprocessing":"part/bom","CreatedDate":"2016-07-22T00:00:00Z","BomPayloadProcessedDate":"2016-07-23T00:00:00Z","Region":"Reynosa","PTCAckSentDate":"2016-07-26T00:00:00Z","PartErrorMsg":"PartErrorMessage"},{"PTCAckMsg":"PTCACkMessage","Description":"CUMMINS PART NPI","BomPayloadProcessed":"true","ModifiedDate":"2016-07-22T00:00:00Z","ProcessedDate":"2016-07-27T00:00:00Z","EcnRequestor":"Administrator","Plant":"Reynosa","UIReprocessingDate":"2016-07-27T00:00:00Z","PartError":"true","id":"0006","BomErrorMsg":"BomErrorMessage","ECNNumber":"116","PtcAck":"false","isErrorDataRequired":"false","Status":"3","OutputXMLEtag":"cdc3b234","PartPayloadProcessed":"false","ERPName":"Symix","Error":"404","BomError":"true","TxnID":"6","PartPayloadProcessedDate":"2016-07-22T00:00:00Z","InputXMLEtag":"cdc3b121","UIReprocessing":"part/bom","CreatedDate":"2016-07-21T00:00:00Z","BomPayloadProcessedDate":"2016-07-23T00:00:00Z","Region":"Reynosa","PTCAckSentDate":"2016-07-26T00:00:00Z","PartErrorMsg":"PartErrorMessage"}],"pagination":{"lastPartition":null,"lastRow":null,"nextPartition":"1!8!U0FQX1BP","nextRow":"1!8!MDAwNw--"}}},"graphData":{"SYMIX":[6,4,2]},"errorData":{"SYMIX":{"series":[{"PTCAckMsg":"PTCACkMessage","Description":"CUMMINS PART NPI","BomPayloadProcessed":"true","ModifiedDate":"2016-07-22T00:00:00Z","ProcessedDate":"2016-07-27T00:00:00Z","EcnRequestor":"Administrator","Plant":"Reynosa","UIReprocessingDate":"2016-07-27T00:00:00Z","PartError":"true","id":"0004","BomErrorMsg":"BomErrorMessage","ECNNumber":"115","PtcAck":"false","isErrorDataRequired":"false","Status":"3","OutputXMLEtag":"cdc3b234","PartPayloadProcessed":"false","ERPName":"Symix","Error":"401","BomError":"true","TxnID":"5","PartPayloadProcessedDate":"2016-07-22T00:00:00Z","InputXMLEtag":"cdc3b121","UIReprocessing":"part/bom","CreatedDate":"2016-07-22T00:00:00Z","BomPayloadProcessedDate":"2016-07-23T00:00:00Z","Region":"Reynosa","PTCAckSentDate":"2016-07-26T00:00:00Z","PartErrorMsg":"PartErrorMessage"},{"PTCAckMsg":"PTCACkMessage","Description":"CUMMINS PART NPI","BomPayloadProcessed":"true","ModifiedDate":"2016-07-22T00:00:00Z","ProcessedDate":"2016-07-27T00:00:00Z","EcnRequestor":"Administrator","Plant":"Reynosa","UIReprocessingDate":"2016-07-27T00:00:00Z","PartError":"true","id":"0006","BomErrorMsg":"BomErrorMessage","ECNNumber":"116","PtcAck":"false","isErrorDataRequired":"false","Status":"3","OutputXMLEtag":"cdc3b234","PartPayloadProcessed":"false","ERPName":"Symix","Error":"404","BomError":"true","TxnID":"6","PartPayloadProcessedDate":"2016-07-22T00:00:00Z","InputXMLEtag":"cdc3b121","UIReprocessing":"part/bom","CreatedDate":"2016-07-21T00:00:00Z","BomPayloadProcessedDate":"2016-07-23T00:00:00Z","Region":"Reynosa","PTCAckSentDate":"2016-07-26T00:00:00Z","PartErrorMsg":"PartErrorMessage"}],"pagination":{"lastPartition":null,"lastRow":null,"nextPartition":"1!8!U0FQX1BP","nextRow":"1!8!MDAwNw--"}}},"userData":{"Role":"Admin","UserName":"Sunil Soni","GlobalId":"csonisk"},"error":false}
			mainGrid = {};errorData = {};erpData={};allGridsData = {};graphData = {};
			if(!data.error){
				allGridsData = data;
				
				errorData = data.errorData;
				graphData = data.graphData;
				var resultSetLength = data.resultSet.symix.series.length;
				var resultFinalIndex = data.resultSet.symix.series.length-1;
				if((data.resultSet.symix.series != undefined) && (data.resultSet.symix.series != null)){
					data.resultSet.symix.pagination["lastPartition"]=data.resultSet.symix.series[resultFinalIndex].partitionKey;
					data.resultSet.symix.pagination["lastRow"]=data.resultSet.symix.series[resultFinalIndex].rowKey;
					lastPartition=data.resultSet.symix.pagination["lastPartition"];
					lastRowKey=data.resultSet.symix.pagination["lastRow"];
				}
				
				$('#username').text(data.userData.rowKey);
				$('#username').attr('data-global-id',data.userData.rowKey);
				$('.username-msg').show();
				$('.logout').show();
				
				commonUtil.removeLoader();
				
				fnGenerateECNSubMenus(data.resultSet);
				
				
				$('.nav-second-level li a').off('click').on('click',function(e){	
					e.stopPropagation();
					e.stopImmediatePropagation();
					var li = $(this).closest('ul').closest('li');
					li.siblings().find('.nav-second-level li a').each(function(){
						$(this).removeClass('active');
					});
					$(this).addClass('active');
					var tab = $(this).closest('.nav-second-level').attr('data-item');
					var grid = $(this).attr('grid');
					var erp = grid.split('-')[1];
					if(tab == "ecn"){
						fnGenerateECNGridHtml(data.resultSet,grid,"ecn",data.userData.role.toUpperCase(),data.graphData,errorData);
						buildECNGrids(erp,data.resultSet);
						buildECNErrorGrids(errorData,erp);
						
					}	
				});
				
				
				
				$('#side-menu li.menu').off('click').on('click',function(e){
					$(this).siblings().find('a').removeClass('active');
					$(this).find('a').not('.nav-second-level a').addClass('active');
					
					var li = $(this);
					li.addClass('active');
					li.siblings().removeClass('active');
					
					var data_tab = $(this).attr('data-tab');
					
					if(data_tab == "home"){
						var menuHtml = "";
						menuHtml +='<li data-tab="home" class="menu active">'+
										'<a href="#" class="active"><i class="fa fa-home fa-fw" ></i> Home</a>'+
									'</li>';
									
						$('#side-menu').html(menuHtml);	
						$('#main1').html('');
						$('#main1').hide();
						$('#main2').hide();
						$('#main0').show();
					}
					else if(data_tab == "ecn"){
						$('#main2').hide();
						$('#main1').show();
						fnGenerateEcnHtml();
						buildECNGraph(graphData);
					}
					else
						$(this).find('.nav-second-level li:first a').trigger('click');
					
				});
				
				$('li[data-tab="ecn"]').trigger('click');
			}
			
		});	
	}	

	function fnGenerateECNSubMenus(ecnData){
		erpData = commonUtil.dashboardErp(ecnData);
		
		var html = "";
		
		for(var i = 0 ; i < erpData.length ; i++){
			html += '<li><a data-toggle="tab" href="#" grid="ecn-'+erpData[i]+'-grid"><span class="menu-text">'+erpData[i]+'</span><span class="selected"></span></a></li>';
		}
		$('#ECNSubMenu').html(html);
		
	}

	function fnGenerateEcnHtml(){
		var html = "";
		
		html += "<div class='graph_outer'>"+
					"<div class='graph_heading'><span>Windchill PLM Dashboard</span></div>"+
					"<div class='graph_content'>";	
		
		html += "<div class='row'>"+
						"<div class='col-sm-12'>"+
							//"<div class='margin-top-10'>"+
								"<div id='highchartContainer' style='height:500px;'></div>"+
							//"</div>"+
						"</div>"+
					"</div>";
					
		html += "</div></div>";			
					
		$('#main1').html(html);		
		
	}

	function fnGenerateECNGridHtml(ecnData,grid,type,role,errorData){
		var html = "";
		
		var newGraphData = {};
		
		var html = "";
		
		var erp = grid.split('-')[1];
		
		
		
		if(graphData){ newGraphData[erp] = graphData[erp] }; 
		var errorCount = 0 ;
		if(graphData){
			errorCount += graphData[erp][1];
		}
		//dividing screen 
		var className = "";
		if(type != "error" && type != "ecn"){className = "max-height";}
		var disabled = "";
		if(type == "ecn" && errorCount > 0 && role == "ADMIN"){
			html += "<div class='row'><div class='col-lg-6'>";
		}
			var label = type.toUpperCase()+" DATA";
			var lblMargin = "margin-left:25%;"
				if(label == "DASHBOARD DATA"){
					label = "ECN DATA";
					lblMargin = "margin-left:15%;"
				}
			
			html += '<div class="row" style="margin-bottom:5px;"><div class="col-xs-9 process-error-header"><span style="'+lblMargin+'">'+label+'</span></div><div class="col-xs-3"><button id="exportBtn" grid="'+type+'-'+erp+'-grid'+'" class="btn btn-info" style="float:right;display:none;">Export To  Excel</button></div></div><div class="row"><div class="col-sm-12"><div id="'+type+erp+'" class="tab-pane fade in active"><div class="pagination-data" style="display:none;"><span class="lastPartition"></span><span class="lastRow"></span><span class="nextPartition"></span><span class="nextRow"></span></div><div class="gridContainer"><div class="row"><div class="col-sm-12"><div id="'+type+'-'+erp+'-grid'+'" class="grid-style '+className+'"></div><div id="'+type+'-'+erp+'-pager'+'" class="pager-style"></div></div></div></div></div></div></div>';
			
			if(type == "ecn"){
				html += "<div class='graph_outer' style='margin-top:10px;'>"+
						"<div class='graph_heading' style='height:28px;'><span>"+erp+"</span><a class='graphIconClick' href='#'><img src='../img/maximize_icon.png'></a></div>"+
						"<div class='graph_content' style='padding:0px;'>";
						
				html += "<div class='row'>"+
							"<div class='col-sm-12'>"+
								//"<div class='margin-top-10'>"+
									"<div id='highchartContainer'></div>"+
								//"</div>"+
							"</div>"+
						"</div>";
						
				html += "</div></div>";		
						
				if(errorCount > 0 && role == "ADMIN"){
					var errorHtml = fnGetECNErrorGridHtml("error",erp);
					
					html += "</div><div class='col-lg-6 errro-data-container'>"+errorHtml+"</div></div>";
				}	
		}				
						
		$('#main1').html(html);	
		
		var id = "main1";
		
		var paginationData = ecnData[erp].pagination;
		
		$('#'+id+' .tab-pane').find('.pagination-data .lastPartition').text(paginationData.lastPartition)
		$('#'+id+' .tab-pane').find('.pagination-data .lastRow').text(paginationData.lastRow)
		$('#'+id+' .tab-pane').find('.pagination-data .nextPartition').text(paginationData.nextPartition)
		$('#'+id+' .tab-pane').find('.pagination-data .nextRow').text(paginationData.nextRow);
		
		if(type == "ecn"){
			buildECNGraph(newGraphData,erpData);
		}
	
	}
	
	function fnGetECNErrorGridHtml(type,erp){
		var html = "";
		html += '<div class="row" style="margin-bottom:5px;"><div class="col-xs-9 process-error-header"><span style="margin-left:15%;">PROCESS ERROR DATA</span></div><div class="col-xs-3"><button id="exportBtn" grid="'+type+'-'+erp+'-grid'+'" class="btn btn-info" style="float:right;display:none;">Export To  Excel</button></div></div><div class="row"><div class="col-sm-12"><div id="'+type+erp+'" class="tab-pane fade in active"><div class="pagination-data" style="display:none;"><span class="lastPartition"></span><span class="lastRow"></span><span class="nextPartition"></span><span class="nextRow"></span></div><div class="gridContainer"><div class="row"><div class="col-sm-12"><div id="'+type+'-'+erp+'-grid'+'" class="grid-style"></div><div id="'+type+'-'+erp+'-pager'+'" class="pager-style"></div></div></div></div></div></div></div>';
		html += '<div class="tableError">'+
		'<p class="details">ECN Details: </p>'+
		'<div class="row" id="form">'+
			'<div>'+
				'<div class="col-sm-12 form-horizontal">'+
					'<div class="form-group">'+
						'<label for="txtBURegion" class="listview-label control-label col-sm-2">Change Log</label>'+
						'<div class="col-sm-10">'+
							'<textarea rows="4" class="form-control "  id="txtDescErr"></textarea>'+
						'</div>'+
					'</div>'+
				'</div>'+
				'<div class="col-sm-12 form-horizontal">'+
					'<div class="form-group">'+
						'<div class="col-sm-7">'+
							'<button type="button" class="btn btn-info col-sm-3 pull-right" id="submitECRErrBtn">Submit</button>'+
						'</div>'+
					'</div>'+
				'</div>'+
			'</div>'+
		'</div>'+
	'</div>';		
		return html ;
		
	}
	
	function fnGenerateEcnErrorGridHtml(errorData,grid,type){
		var html = "";
		var lblMargin = "margin-left:25%;"
		var label = "ECN ERROR DATA";
		var erp = grid.split('-')[1];
		html += '<div class="row"><div class="col-sm-12"><div id="ecn'+type+erp+'" class="tab-pane fade in active"><div class="pagination-data" style="display:none;"><span class="lastPartition"></span><span class="lastRow"></span><span class="nextPartition"></span><span class="nextRow"></span></div><div class="gridContainer"><div class="row"><div class="col-sm-12"><div id="ecn'+type+'-'+erp+'-grid'+'" class="grid-style"></div><div id="ecn'+type+'-'+erp+'-pager'+'" class="pager-style"></div></div></div></div></div></div></div>';
		$('#main2 .ecn-error-container').html(html);
		var id = "main2";
		var paginationData = errorData[erp].pagination;
		$('#'+id+' .tab-pane').find('.pagination-data .lastPartition').text(paginationData.lastPartition)
		$('#'+id+' .tab-pane').find('.pagination-data .lastRow').text(paginationData.lastRow)
		$('#'+id+' .tab-pane').find('.pagination-data .nextPartition').text(paginationData.nextPartition)
		$('#'+id+' .tab-pane').find('.pagination-data .nextRow').text(paginationData.nextRow);
	}

	function buildECNGrids(erp,resultSet){
        var columns = commonUtil.getECNGridColumns();
        var options = commonUtil.getDashboardGridOptions();
        mainGrid['ecn-'+erp+'-grid'] = Object.create(BuildGrid.prototype);
        mainGrid['ecn-'+erp+'-grid'].constructor('#ecn-'+erp+'-grid',resultSet[erp].series,columns,options,'#ecn-'+erp+'-pager');
        mainGrid['ecn-'+erp+'-grid'].createGrid();
	}

	$(document).on('click','#ecnProcessError',function(){
		
		commonUtil.handleHideShow('processErrorScreen');
		
		var grid = (($('.nav.nav-second-level.collapse.in a.active').attr('grid')).replace('ecn','ecnerror'));
		var erp = grid.split('-')[1];
		
		fnGenerateEcnErrorGridHtml(errorData,grid,"error");
		buildECNErrorGrids(errorData,erp);
	});

	function buildECNErrorGrids(resultSet,erp){
		var columns = commonUtil.getECNErrorGridColumns();
        var options = commonUtil.getDashboardGridOptions();
        
        mainGrid['ecnerror-'+erp+'-grid'] = Object.create(BuildGrid.prototype);
             
        mainGrid['ecnerror-'+erp+'-grid'].constructor('#ecnerror-'+erp+'-grid',resultSet[erp].series,columns,options,'#ecnerror-'+erp+'-pager');
		
        mainGrid['ecnerror-'+erp+'-grid'].createGrid();
	}
	
	function buildECNGraph(graphData,erpData,container){
		var graphDataObject={},categoryArr=[], categoryData={},errorGraphData=[], processGraphData=[];
            graphDataObject = graphData;
                
            for(var grph in graphDataObject){
                categoryArr.push(grph);
                errorGraphData.push(graphDataObject[grph][1]);
                
                processGraphData.push(graphDataObject[grph][0]);
                 
            }
            categoryData['error'] = errorGraphData;
            
            categoryData['process'] = processGraphData;
        
            var plotData = commonUtil.prepareECNGraphData(categoryData);

			var name = {"x-axis":"","y-axis":"ECN Purchase Orders","title":"Widchill PLM Dashboard"};
			if(container)
				commonUtil.plotGraph(plotData,name,categoryArr,container);
			else
				commonUtil.plotGraph(plotData,name,categoryArr);
		
	}

	$(document).on('click','.plm .autopager', function(e) {
		  e.stopPropagation();
		  var selfEle = $(this);
    	  var activeGrid = $(this).attr('grid');
		  var url = "";
		  var postObj = {};
		  var mainTab = $('.nav.nav-second-level.collapse.in').attr('data-item');
		  var subTab = "";
		  if(mainTab == "ecn"){
			if($('section.active').attr('data-item-type') == "error")
				url = "";          // error ecn url
			else
				url = "";        //ecn url 
		  }  
		  
		  
		  var erp = ($(this).attr('grid')).split('-')[1];
			  
		  
		  var paginationData = $(this).closest('.tab-pane').find('.pagination-data');
		  
		   if($(paginationData).find('.nextPartition').text() == "null" || $(paginationData).find('.nextRow').text() == "null"){
			  toastr.error('No further data to show');
			  return;
		  }
		  
		  commonUtil.addLoader();	
		  
		  var pagination = {};
		  pagination.lastPartition = $(paginationData).find('.lastPartition').text();
		  pagination.lastRow = $(paginationData).find('.lastRow').text();
		  pagination.nextPartition = $(paginationData).find('.nextPartition').text();
		  pagination.nextRow = $(paginationData).find('.nextRow').text();
		 
		  postObj.paginationParam = pagination;
		  postObj.erpName = erp;
		  postObj.size = 10;
         
          var currentData = mainGrid[$(this).attr('grid')].dataView.getItems();
		  
		  var newData = "";
		  var newpaginationData = "";
		  serviceObj.callToSever(url,JSON.stringify(postObj),"POST").then(function(result,status,xhr){
				  console.log("success");
				  commonUtil.removeLoader();
				  var data = result ;
				  if(mainTab == "ecn"){
					  if(subTab == "home"){
						newData = data.resultSet[erp].series; 
						newpaginationData = data.resultSet[erp].pagination;  
					  }
					  else{
						newData = data.errorData[erp].series; 
						newpaginationData = data.errorData[erp].pagination;  
					  }
					  newData = fnChangeStatus(newData); 		
					  
				  }  
				  
				  
					$(paginationData).find('.lastPartition').text(newpaginationData.lastPartition);
					$(paginationData).find('.lastRow').text(newpaginationData.lastRow);
					$(paginationData).find('.nextPartition').text(newpaginationData.nextPartition);
					$(paginationData).find('.nextRow').text(newpaginationData.nextRow);
					
					$.merge(currentData,newData);
					mainGrid[activeGrid].grid.invalidate();
					mainGrid[activeGrid].updateGrid(currentData);
					commonUtil.resizeCanvas(activeGrid);
					
					if(newpaginationData.nextRow == "null" && newpaginationData.nextPartition == "null" )
						$(selfEle).remove();
		  });
	});	
	
	function getDateTime() {
	    var date = new Date();
	    return date.getFullYear() + "/" + (date.getMonth() + 1) + "/" + date.getDate() + " " +  date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds();
	}
	
	$(document).on('click','#submitECRErrBtn', function(e) {
		var poNumArray = [];
		
		//var activeGrid = $('#main2 .tab-pane.fade.in.active .grid-style').attr('id');
		var activeGrid = ($('.nav.nav-second-level.collapse.in a.active').attr('grid')).replace('ecn','error');
		
		var selectedRows = mainGrid[activeGrid].grid.getSelectedRows();
		
		if(selectedRows.length == 0){
			toastr.error('Select PO to be processed');
			return;
		}
		
		var selectedErrorItem = [];
		
		for(var i = 0 ; i < selectedRows.length ;i++){
			var item = mainGrid[activeGrid].grid.getDataItem(selectedRows[i]);
			item.uiprocessedBy = $('#username').text();
			item.uiprocessedDate = getDateTime();
			item.uiprocessingComments = $('#txtDescErr').val();
			item.uiprocessed = 1;
			item.code = 200;
			item.isErrored = 0;
			item.status = "success";
			selectedErrorItem.push(item);
			poNumArray.push(item.ECNNumber);
		}
		var erp = activeGrid.split('-')[1];	
		console.log(poNumArray);
		var userName = $('#username').text();
		var globalId = $('#username').attr('data-global-id');
		
		//var sendObjStr = {"ECNNumber":"", "TransactionID":"", "Plant":"", "ECNDescription":"", "ECNType":"", "UIProcessingComments":$("#txtDescErr").val()};
		
		/*var sendObjStr = {"IsProcessed": "", "IsErrored":0, "Message":"", "Code":200, "Status":"", "ECNNumber":"", "TransactionID":"", "Erp":erp, "Region":"", "Plant":"", "CreatedBy":"", "CreatedDate":"", 
							"ProcessedDate":"", "ProcessedBy":"", "IsAcknowledged":0, "AcknowledgementStatus":"", "AcknowledgementCode":"", "AcknowledgementMessage":"", "AcknowledgementDate":"", 
							"UIProcessed": 1, "UIProcessedBy":"", "UIProcessedDate":"", "UIProcessingComments":comment, "ECNDescription":"", "ECNType":""}*/
		
		var dbObj = {"series":selectedErrorItem, "pagination":null};
		var sendArrayAsString = encodeURIComponent(JSON.stringify(dbObj));
		commonUtil.addLoader(); // /getPLMDetails
		
		jQuery.ajax({
			url:'/reprocessWebportalXML',
			data:"data="+sendArrayAsString,
			method:'POST',
			success: function(response){
				console.log(response);
				commonUtil.removeLoader();
				if(response){
					toastr.success("Processed Error Data Successfully.");
				}else{
					toastr.error("Processed Error Data Failed.");
				}
				EcnInit();
				//jQuery('span.menu-text').first().click(); 
			}
		}).fail(function() {
			toastr.error("Connection Problem During Processing Error Data.");
		});
			
		/*serviceObj.callToSever("/reprocessWebportalXML",selectedErrorItem,'POST').then(function(){
			commonUtil.removeLoader();  
				//console.log(result);  
				var successList = result.resultSet; //result.successList
				var errorList = result.errorData;
				console.log(successList);console.log(errorList);
				if(successList.length > 0 || errorList.length > 0){
					
					if(successList.length > 0){
						mainGrid[activeGrid].deleteItems(successList);
						$('#main2 .tab-pane.fade.in.active input[type="checkbox"]').each(function(){
							$(this).attr('checked',false);
						});
						var dashboardGrid = activeGrid.replace('ecnerror','ecn');
						mainGrid[dashboardGrid].updateItems(successList);
						graphData = result.graphData;
						var newGraphData = {};
						newGraphData[erp] = graphData[erp];
						buildECNGraph(newGraphData);
						var errorCount = newGraphData[erp][1];
						$('#ecrErrorCount').text(errorCount);
						//$('#txtDescErr').val('');
						$('#txtXML').val(); 
					}
					var sl = "",slMsg = "",el="",elMsg="";
					if(successList.length > 0){
						sl = successList.join(",");
						slMsg = "Processing Error Request success for "+sl;
					}
					if(errorList.length > 0){
						el = errorList.join(",")
						elMsg = "Error occured while processing process "+el;
					}
					
					if(slMsg)
						toastr.success(slMsg);
					
					if(elMsg)
						toastr.error(elMsg);
					
				}
		});*/
	});
	
	//Graph UI Code.
	$(document).on('click','.graphIconClick',function(){
		var erp = ($('.nav.nav-second-level.collapse.in li a.active').attr('grid')).split('-')[1];
		
		var type = "";
		
		if($('#main1').hasClass('sc'))
			type = "sc";
		else
			type = "plm";
		
		var html = "";
		
		html += "<div class='graph_outer'>"+
					"<div class='graph_heading'><span>"+erp+"</span></div>"+
					"<div class='graph_content'>";	
		
		html += "<div class='row'>"+
						"<div class='col-sm-12'>"+
							//"<div class='margin-top-10'>"+
								"<div id='modal-highchartContainer' style='height:500px;width:70%;'></div>"+
							//"</div>"+
						"</div>"+
					"</div>";
					
		html += "</div></div>";			
					
		$('#myModal .modal-body').html(html);

		var newGraphData = {};
		newGraphData[erp] = graphData[erp];
		if(type == "sc")
			buildGraph(newGraphData,"","modal-highchartContainer");
		else
			buildECNGraph(newGraphData,"","modal-highchartContainer");
		
		$('#myModal').modal('show');
	})
	
	 function buildECNErrorGrids(resultSet,erp){
        //var mainGrid = {};
        
         //getting the columns and grid options via the commonUtil API
       
        //buildContainer('error',erpData,resultSet);
		if($('#error-'+erp+'-grid').length > 0){
			var columnsErr = commonUtil.getECNErrorGridColumns();
			var optionsErr = commonUtil.getECNProcessErrorGridOptions();
			//for(var i=0;i<erpData.length;i++){
				 mainGrid['error-'+erp+'-grid'] = Object.create(BuildGrid.prototype);
				 
				 mainGrid['error-'+erp+'-grid'].constructor('#error-'+erp+'-grid', resultSet[erp].series,columnsErr,optionsErr,'#error-'+erp+'-pager');
				 mainGrid['error-'+erp+'-grid'].createGrid();
			//}
		}
      
}

})(jQuery);