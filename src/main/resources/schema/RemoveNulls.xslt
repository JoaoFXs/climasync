<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">

    <!-- Aplique a transformação em todo o XML -->
    <xsl:template match="/">
        <xsl:apply-templates/>
    </xsl:template>

    <!-- Caso a tag tenha o conteúdo 'null', substitua por <tag xsi:nil="true"/> -->
    <xsl:template match="*">
        <xsl:choose>
            <xsl:when test="normalize-space(.) = 'null'">
                <xsl:element name="{name()}">
                    <xsl:attribute name="xsi:nil">true</xsl:attribute>
                </xsl:element>
            </xsl:when>
            <xsl:otherwise>
                <xsl:element name="{name()}">
                    <xsl:apply-templates/>
                </xsl:element>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

</xsl:stylesheet>
