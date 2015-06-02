<?xml version="1.0"?>
<xsl:stylesheet  
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0"> 
    <xsl:output method="xml"/>    
    <xsl:template match="//*[local-name()='state' or local-name()='final']" >
        <field>
            <xsl:attribute name="name">
                <xsl:value-of select="@id"/>
            </xsl:attribute>
            <xsl:apply-templates select="*[local-name()='onentry']"/>
            <xsl:apply-templates select="*[local-name()='onexit']"/>
            <nomatch count="3">
                <exit/>
            </nomatch>
            <noinput count="3">
                <exit/>
            </noinput>
        </field>       
    </xsl:template>    
    
    <xsl:template match="*[local-name()='onentry']">
        <xsl:copy-of select="node()"/> 
    </xsl:template>
    
    <xsl:template match="*[local-name()='onexit']">
        <filled>
            <xsl:copy-of select="node()"/> 
        </filled>
    </xsl:template>
</xsl:stylesheet>