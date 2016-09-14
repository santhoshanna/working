<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" version="1.0" encoding="UTF-8"
		indent="yes" />
	<xsl:strip-space elements= "*" />

	<!-- identity transform -->
	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" >
			</xsl:apply-templates>
		</xsl:copy>
	</xsl:template>
	
	
	
	<xsl:template match="COLLECTION">
        <xsl:copy>
         <xsl:apply-templates select="@*|node()" />
            <BOMCOMPONENTS-BOMCOMPONENTS>
			<xsl:apply-templates
				select="./DeletedBOMComponents | ./AddedBOMComponents | ./ChangedBOMComponents | ./AddedBOMs/BOMHeader"
				mode="move" />
            </BOMCOMPONENTS-BOMCOMPONENTS>
            <PARTS-PARTS>
			<xsl:apply-templates
				select="./AddedParts | ./ChangedParts | ./UnchangedParts" mode="move" />
            </PARTS-PARTS>
        </xsl:copy>
    </xsl:template>
    
	<xsl:template
		match="DeletedBOMComponents | AddedBOMComponents | ChangedBOMComponents | AddedBOMs/BOMHeader" />
	
	<xsl:template match="DeletedBOMComponents" mode="move">
		<BOMComponent>
		<ACTION>D</ACTION>
			<xsl:apply-templates select="@*|node()" />
		</BOMComponent>
	</xsl:template>
	<xsl:template match="AddedBOMComponents" mode="move">
		<BOMComponent>
		<ACTION>A</ACTION>
			<xsl:apply-templates select="@*|node()" />
		</BOMComponent>
	</xsl:template>
	<xsl:template match="ChangedBOMComponents" mode="move">
		<BOMComponent>
		<ACTION>C</ACTION>
			<xsl:apply-templates select="@*|node()" />
		</BOMComponent>
	</xsl:template>
	<xsl:template match="BOMHeader" mode="move">
		<xsl:copy>
		<ACTION>Head</ACTION>
			<xsl:apply-templates select="@*|node()" />
		</xsl:copy>
	</xsl:template>

	<xsl:template match="AddedParts | ChangedParts | UnchangedParts" />
	<xsl:template match="AddedParts" mode="move">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />
		</xsl:copy>
	</xsl:template>
	<xsl:template match="ChangedParts" mode="move">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />
		</xsl:copy>
	</xsl:template>
	<xsl:template match="UnchangedParts" mode="move">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />
		</xsl:copy>
	</xsl:template>

	<xsl:template match="BOMHeader/Number">
		<END-ITEM>
			<xsl:apply-templates select="@*|node()" />
		</END-ITEM>
	</xsl:template>

	<!-- All the above are the XML Moving elements in BOMCOMPONENT-BOMCOMPONENT and PART-PART -->

	<xsl:template match="LineNumber">
		<SEQ>
			<xsl:apply-templates select="@*|node()" />
		</SEQ>
	</xsl:template>

	<xsl:template match="BOMComponent">
		<xsl:copy>
			<xsl:apply-templates select="../../AddedBOMs/BOMHeader/StartEffectivity"
				mode="move" />
			<xsl:apply-templates select="../../AddedBOMs/BOMHeader/EndEffectivity"
				mode="move" />    <!-- No need of this operation as we already moving BOMHeader inside bom compoments -->
			<xsl:apply-templates select="@*|node()" />
		</xsl:copy>
	</xsl:template>

	<xsl:template match="StartEffectivity" mode="move">
		<StartEffectivity>
			<xsl:apply-templates select="@*|node()" />
		</StartEffectivity>
	</xsl:template>

	<xsl:template match="EndEffectivity" mode="move">
		<EndEffectivity>
			<xsl:apply-templates select="@*|node()" />
		</EndEffectivity>
	</xsl:template>

	<xsl:template match="AddedBOMComponents/BOMComponent/PartNumber">
		<xsl:copy>
			<xsl:apply-templates select="@* | node()" />
		</xsl:copy>
		<xsl:apply-templates
			select="/COLLECTION/UnchangedParts/Part[Number = current()]/MaterialType" />
	</xsl:template>
		
	<!-- Identity transform template -->
	<xsl:template match="@* | node()">
		<xsl:copy>
			<xsl:apply-templates select="@* | node()" />
		</xsl:copy>
	</xsl:template>


	<xsl:template match="Part">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />
			<xsl:apply-templates select="../../AddedECN/ECNHeader/Number"
				mode="move" />
		</xsl:copy>
	</xsl:template>
	<xsl:template match="Number" mode="move">
		<DRAWING-NBR>
			<xsl:apply-templates select="@*|node()" />
		</DRAWING-NBR>
	</xsl:template>

	<xsl:template match="Weight">
		<UNIT-WEIGHT>
			<xsl:value-of
				select="translate(., translate(., '.0123456789', ''), '') div 0.45359237" />
		</UNIT-WEIGHT>
	</xsl:template>

	<xsl:template match="WeightUnits">
		<WEIGHT-UNITS>lbs</WEIGHT-UNITS>
	</xsl:template>

	<xsl:template match="CountryOfOrigin">
		<COUNTRY-OF-ORIGIN>
			<xsl:choose>
				<xsl:when test="../../Part/CountryOfOrigin='MX'">
					MEXICO
				</xsl:when>
				<xsl:when test="../../Part/CountryOfOrigin='US'">
					United States
				</xsl:when>
			</xsl:choose>
		</COUNTRY-OF-ORIGIN>
	</xsl:template>
</xsl:stylesheet>
