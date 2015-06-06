<?xml version="1.0"?>
<xsl:stylesheet  
    xmlns="http://www.w3.org/2001/vxml"
    xmlns:scxml="http://www.w3.org/2005/07/scxml"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"    
    version="1.0"
    exclude-result-prefixes="scxml"> 
    <xsl:output method="xml"/>    
    <xsl:template match="//scxml:state|//scxml:final" >
        <initial>
            <xsl:attribute name="name">
                <xsl:value-of select="@id"/>
            </xsl:attribute>
            <xsl:apply-templates select="scxml:onentry" />
            <xsl:apply-templates select="scxml:onexit" />
            <nomatch count="3">
                <exit/>
            </nomatch>
            <noinput count="3">
                <exit/>
            </noinput>
        </initial>       
    </xsl:template>    
    
    <xsl:template match="scxml:onentry">
        <xsl:apply-templates  select="node()" mode="copy-no-namespaces"/>
        <!--   <xsl:copy-of select="node()"/> -->
    </xsl:template>
    
    <xsl:template match="scxml:onexit">
        <filled>
            <xsl:apply-templates  select="node()" mode="copy-no-namespaces"/>
            <!--     <xsl:copy-of select="node()"/> -->
        </filled>
    </xsl:template>
    
    <xsl:template match="*" mode="copy-no-namespaces">
        <xsl:element name="{local-name()}" namespace="{namespace-uri()}">
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates select="node()" mode="copy-no-namespaces"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="comment()| processing-instruction()" mode="copy-no-namespaces">
        <xsl:copy/>
    </xsl:template>
</xsl:stylesheet>