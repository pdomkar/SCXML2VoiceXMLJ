<?xml version="1.0"?>
<xsl:stylesheet  
    xmlns="http://www.w3.org/2001/vxml"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:scxml="http://www.w3.org/2005/07/scxml"
    version="1.0"> 
    <xsl:output method="xml"/>    
    <xsl:template match="//*[local-name()='state' or local-name()='final']" >
        <field>
            <xsl:attribute name="name">
                <xsl:value-of select="@id"/>
            </xsl:attribute>
            <xsl:apply-templates select="scxml:onentry"/>
            <xsl:apply-templates select="scxml:onexit"/>
            <nomatch count="3">
                <exit/>
            </nomatch>
            <noinput count="3">
                <exit/>
            </noinput>
        </field>       
    </xsl:template>    
    
    <xsl:template match="scxml:onentry">
        <xsl:copy-of select="node()"/> 
    </xsl:template>
    
    <xsl:template match="scxml:onexit">
        <filled>
            <xsl:copy-of select="node()"/> 
        </filled>
    </xsl:template>
</xsl:stylesheet>