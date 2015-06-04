<?xml version="1.0"?>
<xsl:stylesheet  
    xmlns="bar"
    xmlns:foo="foo"
    version="1.0"> 
    <xsl:output method="xml"/>    
    
    <xsl:template match="foo:someFoo">
        <someBar/>
    </xsl:template>
  
</xsl:stylesheet>