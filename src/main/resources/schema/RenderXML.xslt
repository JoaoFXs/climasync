<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:param name="nameLocation"/>
<xsl:param name="detailLocation"/>
<xsl:template match="/">
  <SixteenDayForecast>
  <Header>
  	<Location_Information>
		<Namelocation><xsl:value-of select="$nameLocation"/></Namelocation>
		<DetailLocation><xsl:value-of select="$detailLocation"/></DetailLocation>
		<Timezone><xsl:value-of select="root/timezone"/></Timezone>
		<Latitude><xsl:value-of select="root/latitude"/></Latitude>
		<Longitude><xsl:value-of select="root/longitude"/></Longitude>
	</Location_Information>
  </Header>
  
  <!-- Loop para cada time -->
        <xsl:for-each select="root/daily/time">
          
            <Daily>
         
            <xsl:variable name="index" select="position()" />
            <Time><xsl:value-of select="."/></Time>
            
            <SunInformation>
			    <Sunrise><xsl:value-of select="concat(../sunrise[$index], ':00')"/></Sunrise>
			    <Sunset><xsl:value-of select="concat(../sunset[$index], ':00')"/></Sunset>
			    <SunshineDuration><xsl:value-of select="../sunshine_duration[$index]"/></SunshineDuration>
			    <DaylightDuration><xsl:value-of select="../daylight_duration[$index]"/></DaylightDuration>       
	            <ApparentTemperatureMin><xsl:value-of select="../apparent_temperature_min[$index]" /></ApparentTemperatureMin>
            </SunInformation>
            
            <WeatherInformation>
                <ApparentTemperatureMin><xsl:value-of select="../apparent_temperature_min[$index]"/></ApparentTemperatureMin>
			    <ApparentTemperatureMax><xsl:value-of select="../apparent_temperature_max[$index]"/></ApparentTemperatureMax>
			    <TemperatureMin><xsl:value-of select="../temperature_2m_min[$index]"/></TemperatureMin>
			    <TemperatureMax><xsl:value-of select="../temperature_2m_max[$index]"/></TemperatureMax>       
            </WeatherInformation>
            
             <WindInformation>
                <WindSpeedMax><xsl:value-of select="../wind_speed_10m_max[$index]"/></WindSpeedMax>
			    <WindGustsMax><xsl:value-of select="../wind_gusts_10m_max[$index]"/></WindGustsMax>
			    <WindDirectionDominant><xsl:value-of select="../wind_direction_10m_dominant[$index]"/></WindDirectionDominant>    
            </WindInformation>
            
            <PrecipitationInformation>
                <RainSum><xsl:value-of select="../rain_sum[$index]"/></RainSum>
			    <PrecipitationSum><xsl:value-of select="../precipitation_sum[$index]"/></PrecipitationSum>
			    <PrecipitationProbabilityMax><xsl:value-of select="../precipitation_probability_max[$index]"/></PrecipitationProbabilityMax>
			    <PrecipitationHours><xsl:value-of select="../precipitation_hours[$index]"/></PrecipitationHours>       
			    <ShowerSum><xsl:value-of select="../showers_sum[$index]"/></ShowerSum>   
            </PrecipitationInformation>
            
            <UVInformation>
                <UvIndexMax><xsl:value-of select="../uv_index_max[$index]"/></UvIndexMax>
			    <UvIndexClearSkyMax><xsl:value-of select="../uv_index_clear_sky_max[$index]"/></UvIndexClearSkyMax>
			    <ShortWaveRadiationSum><xsl:value-of select="../shortwave_radiation_sum[$index]"/></ShortWaveRadiationSum>     
            </UVInformation>
            
             <Others>
                <Evapotranspiration><xsl:value-of select="../et0_fao_evapotranspiration[$index]"/></Evapotranspiration>
			    <SnowfallSum><xsl:value-of select="../snowfall_sum[$index]"/></SnowfallSum>
            </Others>
          </Daily>
        </xsl:for-each>
<!--     <h2>My CD Collection</h2> -->
<!--     <table border="1"> -->
<!--       <tr bgcolor="#9acd32"> -->
<!--         <th>Title</th> -->
<!--         <th>Artist</th> -->
<!--       </tr> -->
<!--       <xsl:for-each select="catalog/cd"> -->
<!--         <tr> -->
<!--           <td><xsl:value-of select="title"/></td> -->
<!--           <td><xsl:value-of select="artist"/></td> -->
<!--         </tr> -->
<!--       </xsl:for-each> -->
<!--     </table> -->
<!--   </Header> -->
  </SixteenDayForecast>
</xsl:template>

</xsl:stylesheet>