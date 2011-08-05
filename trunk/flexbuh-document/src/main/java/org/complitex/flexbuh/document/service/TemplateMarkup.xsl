<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml" encoding="windows-1251"/>

    <xsl:template match="xsl:stylesheet">
        <xsl:copy>
            <xsl:text disable-output-escaping="yes">
                <![CDATA[
                    <xsl:output method="xml" encoding="windows-1251" omit-xml-declaration="yes"/>
                ]]>
            </xsl:text>

            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="xsl:template[@match = 'DECLAR']">
        <xsl:copy>
            <xsl:attribute name="match">DECLAR</xsl:attribute>

            <html>
                <head>
                    <title><xsl:value-of select="//title"/></title>
                </head>

                <xsl:copy-of select="//body"/>
            </html>
            
        </xsl:copy>
    </xsl:template>

    <xsl:template match="xsl:template">
        <xsl:copy-of select="."/>
    </xsl:template>
</xsl:stylesheet>