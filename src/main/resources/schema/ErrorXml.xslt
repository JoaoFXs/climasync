<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:param name="status"/>
<xsl:param name="errorCode"/>
<xsl:param name="errorDescription"/>

<xsl:template match="/">
  <WeatherForecastResponse>
	  <Body>
	  		<Status><xsl:value-of select="$status"/></Status>
	  		<ErrorCode><xsl:value-of select="$errorCode"/></ErrorCode>
	  		<ErrorDescription><xsl:value-of select="$errorDescription"/></ErrorDescription>
	  </Body>
  </WeatherForecastResponse>
  
</xsl:template>

</xsl:stylesheet>