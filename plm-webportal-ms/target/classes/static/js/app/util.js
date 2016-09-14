/*
file: util.js
date: May 26, 2016
file purpose: common methods encapsulated in IIFE. 
server calls: none
*/

/*
* To expose an API for use by other sections of code via variable commonUtil
* @param {Object} jQuery 
* @return {Object} an object which exposes the methods
*/
var commonUtil = (function($){
    
    
   /*
    * To add a loading sign on the page
    */
    function addLoader(){
        $('#loadingindicator').addClass('wait');
    }
    
    /*
     * To remove a loading sign from the page
     */
    function removeLoader(){
        $('#loadingindicator').removeClass('wait');
    }
    
    /*
     * To handle hide and show of the 2 sections (Dashboard and process error screen)
     * @param {String} screen 
     */
    function handleHideShow(screen){
        if(screen === 'dashboardScreen'){
            $("#main1").css("display","block");
            $("#main2").css("display","none");
			$("#main2").removeClass('active');
			$("#main1").addClass('active');
        }
        else if(screen === 'processErrorScreen'){
            $("#main1").css("display","none");
            $("#main2").css("display","block");
			$("#main2").addClass('active');
			$("#main1").removeClass('active');
        }		
    }
    
    /*
     * To get the column definition for the dashboard grid
     * @return {Array} column definition array of objects
     */
    
    function xmlFormatter(row, cell, value, columnDef, dataContext) {
    	  return "<a href='http://m2330338.asia.jci.com:8765/plm-subscriber-ms/downloadXML?filename="+dataContext.ecnnumber+".xml&ecnnumber="+dataContext.ecnnumber+"'>XML</a>";
    	}
	
	function getECNGridColumns(){
		return [{
			id: "ECNNumber",
            name: "Change Number",
            field: "ecnnumber",
            minWidth: 140,
            resizable:true,
	        cssClass:'alignvaluesCenter'
		},
		{
			id: "Description",
            name: "Description",
            field: "ecndescription",
            minWidth: 200,
            resizable:true,
	        cssClass:'alignvaluesCenter'
		},
		{
			id: "Plant",
            name: "Plant",
            field: "plant",
            minWidth: 130,
            resizable:true,
	        cssClass:'alignvaluesCenter'
		},
		{
			id: "Status",
            name: "Status",
            field: "status",
            minWidth: 150,
            resizable:true,
	        cssClass:'alignvaluesCenter'
		},
		{
			id: "xmlDownload",
			name: "Download XML",
	        field: "xmlDownload",
	        sortable: false,
	        width: 130,
	        formatter:xmlFormatter,
	        cssClass:'alignvaluesCenter'
		}
		];
	}
	
	function getECNErrorGridColumns(){
		return [{
			id: "ECNNumber",
            name: "Change Number",
            field: "ecnnumber",
            minWidth: 130,
            resizable:true,
	        cssClass:'alignvaluesCenter'
		},
		{
			id: "Plant",
            name: "Plant",
            field: "plant",
            minWidth: 40,
            resizable:true,
	        cssClass:'alignvaluesCenter'
		},
		{
			id: "ChangeType",
            name: "Change Type",
            field: "ecntype",
            minWidth: 150,
            resizable:true,
	        cssClass:'alignvaluesCenter'
		},
		{
			id: "Status",
            name: "Status",
            field: "status",
            minWidth: 100,
            resizable:true,
	        cssClass:'alignvaluesCenter'
		},
		{
			id: "EcnRequestor",
            name: "ECN Requestor",
            field: "createdBy",
            minWidth: 150,
            resizable:true,
	        cssClass:'alignvaluesCenter'
		},
		{
			id: "CreatedDate",
            name: "ESI TRX Date",
            field: "createdDate",
            minWidth: 200,
            resizable:true,
	        cssClass:'alignvaluesCenter'
		},
		{
			id: "ProcessedDate",
            name: "Processed Date",
            field: "processedDate",
            minWidth: 200,
            resizable:true,
	        cssClass:'alignvaluesCenter'
		}
		];
	}
	
    /*
     * To get the options for the dashboard grid
     * @return {Object} options object
     */
    function getDashboardGridOptions(){
      return {
            enableCellNavigation: true,
            enableColumnReorder: false,
			enableAddRow: false,
			//rowHeight:30
			//explicitInitialization: true
        };
    }
    
    
    function getECNProcessErrorGridOptions(){
        return {
             enableCellNavigation: true,
             enableColumnReorder: false,
 			enableAddRow: false,
 			//rowHeight:30
         };
     }

    function getDashboardGridReference(){
        return e2OpenDashboard.mainGrid();
    }
    
    /*
     * To get the data in the format required by the method to process PO data
     * @param {Object} dataView 
     * @param {Array} records
     * @param {Boolean} descFlag
     * @return {Object} processed data
     */
    function getProcessData(dataView,records, descFlag){
        var dataToProcess = [], idToPoNumMap = {},descGlobalText = {};
        var gridData = dataView.getItems() || [];
        for(var i=0, l = records.length; i< l ; i++){
            var item = dataView.getItemById(records[i]);
            idToPoNumMap[item["poNum"]] = parseInt(item["poId"]);
            dataToProcess.push(item["poNum"]);
            if(descFlag){
                descGlobalText[item["poNum"]] = parseInt(item["poId"]);
            }
        }

        return(JSON.stringify({
            poNums: dataToProcess,
            poNumToIdMap : idToPoNumMap,
            Description : descGlobalText
        }));
    }
    
    /*
     * To update the error count label in the dashboard screen
     * @param {String} errorCount 
     */
    function updateErrorCount(errorCount){
        $('#errorCount').text(errorCount);
    }
    
    function resizeCanvas(grid){
        setTimeout(function(){
            e2OpenDashboard.mainGrid()[grid].grid.resizeCanvas();
        },300);
        
    }
    
    /*
     * To get the options for the dashboard grid
     * @return {Object} options object
     */
    function getDashboardGridOptions(){
      return {
            enableCellNavigation: true,
            enableColumnReorder: false,
			enableAddRow: false,
			//rowHeight:30
			//explicitInitialization: true
        };
    }
    
    /*
     * To prepare graph data, to be used to plot the graph showing the statistics based on the Status field
     * @param {Number} processedCount 
     * @param {Number} inTransitCount
     * @param {Number} errorCount
     * @return {Array} graph data
     */
	 
	 //Sunil:Pass dynamically-step1
	
   function prepareGraphData(data){
        return [{
                name: 'Processed',
                data: data.error,
                color: "#90ed7d"
            }, {
                name: 'In-Transit',
                data: data.transit,
                color: "#7cb5ec"

            }, {
                name: 'Errored',
                data: data.process,
                color: "#AA4643"
            }];
    }
	
	function prepareECNGraphData(data){
        return [{
                name: 'Processed',
                data: data.process,
                color: "#90ed7d"
            }, {
                name: 'Errored',
                data: data.error,
                color: "#AA4643"
            }];
    }
    
    
    function createGraphData(graphDataObject){
        return graphDataObject;
        
    }
    
    /*
     * To get the status text based on the status code
     * @param {Number} statusCode 
     * @return {String} status text
     */
    function getStatusText(statusCode){
        if(statusCode === 1){
            return "In-transit";
        }
        else if(statusCode === "success"){
            return "Transaction Completed";
        }
        else if(statusCode === "failed"){
            return "Error in Process";
        }
    }
    
    /*
     * To get the graph container id
     * @return {String} graph container id
     */
    function getGraphContainer(){
        return "highchartContainer";
    }
    
    /*
     * To plot the graph showing the statistics based on the Status field
     * @param {Array} plotData 
     */
    function plotGraph(plotData,name,categoryArr,container){
        var highchartContainer = "";
		if(container)
			highchartContainer = container;
		else
			highchartContainer = getGraphContainer();
		
        if(plotData){
            graphObj.createChart(highchartContainer, plotData,name,categoryArr);
        }
        else{
            graphObj.createChart(highchartContainer,null,name,categoryArr);
        }
        
    }
    
    /*
     * 
     * @param {Object} dashboardData 
     */
    function dashboardErp(dashboardData){
        var erpData = [];
        for(var d in dashboardData){
            erpData.push(d);
        }
        return erpData;
    }
    
    $(document).on('click','.tabUl li a', function(e){
        var grid = $(this).attr('grid');
		var maingrid = e2OpenDashboard.mainGrid();
		var curGrid = maingrid[grid].grid;
		
		var filterPlugin = new Ext.Plugins.HeaderFilter({});

		// This event is fired when a filter is selected
		filterPlugin.onFilterApplied.subscribe(function () {
			maingrid[grid].dataView.refresh();
			curGrid.resetActiveCell();

			// Excel like status bar at the bottom
			var status;

			if (maingrid[grid].dataView.getLength() === maingrid[grid].dataView.getItems().length) {
				status = "";
			} else {
				status = maingrid[grid].dataView.getLength() + ' OF ' + maingrid[grid].dataView.getItems().length + ' RECORDS FOUND';
			}
			$('#status-label').text(status);
		});

		// Event fired when a menu option is selected
		filterPlugin.onCommand.subscribe(function (e, args) {
			maingrid[grid].dataView.fastSort(args.column.field, args.command === "sort-asc");
		});

		curGrid.registerPlugin(filterPlugin);

		curGrid.init();
        resizeCanvas(grid);
    })
    
    return{
        addLoader : addLoader,
        removeLoader : removeLoader,
        handleHideShow : handleHideShow,
        getDashboardGridReference : getDashboardGridReference,
        getProcessData : getProcessData,
        getDashboardGridOptions: getDashboardGridOptions,
        updateErrorCount : updateErrorCount,
        prepareGraphData : prepareGraphData,
        getStatusText : getStatusText,
        getGraphContainer : getGraphContainer,
        plotGraph : plotGraph,
        dashboardErp : dashboardErp,
		resizeCanvas:resizeCanvas,
		getECNGridColumns:getECNGridColumns,
		prepareECNGraphData:prepareECNGraphData,
		getECNErrorGridColumns:getECNErrorGridColumns,
		getECNProcessErrorGridOptions:getECNProcessErrorGridOptions
    }

})(jQuery);