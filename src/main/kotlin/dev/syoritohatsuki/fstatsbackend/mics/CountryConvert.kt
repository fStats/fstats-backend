package dev.syoritohatsuki.fstatsbackend.mics

val oldName2ISO by lazy {
    mapOf(
        "Andorra" to "AND",
        "United Arab Emirates (the)" to "ARE",
        "Afghanistan" to "AFG ",
        "Antigua and Barbuda" to "ATG",
        "Anguilla" to "AIA",
        "Albania" to "ALB",
        "Armenia" to "ARM",
        "Angola" to "AGO",
        "Antarctica" to "ATA",
        "Argentina" to "ARG",
        "American Samoa" to "ASM",
        "Austria" to "AUT",
        "Australia" to "AUS",
        "Aruba" to "ABW",
        "Åland Islands" to "ALA",
        "Azerbaijan" to "AZE",
        "Bosnia and Herzegovina" to "BIH",
        "Barbados" to "BRB",
        "Bangladesh" to "BGD",
        "Belgium" to "BEL",
        "Burkina Faso" to "BFA",
        "Bulgaria" to "BGR",
        "Bahrain" to "BHR",
        "Burundi" to "BDI",
        "Benin" to "BEN",
        "Saint Barthélemy" to "BLM",
        "Bermuda" to "BMU",
        "Brunei Darussalam" to "BRN",
        "Bolivia (Plurinational State of)" to "BOL",
        "Bonaire, Sint Eustatius and Saba" to "BES",
        "Brazil" to "BRA",
        "Bahamas (the)" to "BHS",
        "Bhutan" to "BTN",
        "Bouvet Island" to "BVT",
        "Botswana" to "BWA",
        "Belarus" to "BLR",
        "Belize" to "BLZ",
        "Canada" to "CAN",
        "Cocos (Keeling) Islands (the)" to "CCK",
        "Congo (the Democratic Republic of the)" to "COD",
        "Central African Republic (the)" to "CAF",
        "Congo (the)" to "COG",
        "Switzerland" to "CHE",
        "Côte d'Ivoire" to "CIV",
        "Cook Islands (the)" to "COK",
        "Chile" to "CHL",
        "Cameroon" to "CMR",
        "China" to "CHN",
        "Colombia" to "COL",
        "Costa Rica" to "CRI",
        "Cuba" to "CUB",
        "Cabo Verde" to "CPV",
        "Curaçao" to "CUW",
        "Christmas Island" to "CXR",
        "Cyprus" to "CYP",
        "Czechia" to "CZE",
        "Germany" to "DEU",
        "Djibouti" to "DJI",
        "Denmark" to "DNK",
        "Dominica" to "DMA",
        "Dominican Republic (the)" to "DOM",
        "Algeria" to "DZA",
        "Ecuador" to "ECU",
        "Estonia" to "EST",
        "Egypt" to "EGY",
        "Western Sahara*" to "ESH",
        "Eritrea" to "ERI",
        "Spain" to "ESP",
        "Ethiopia" to "ETH",
        "Finland" to "FIN",
        "Fiji" to "FJI",
        "Falkland Islands (the) [Malvinas]" to "FLK",
        "Micronesia (Federated States of)" to "FSM",
        "Faroe Islands (the)" to "FRO",
        "France" to "FRA",
        "Gabon" to "GAB",
        "United Kingdom of Great Britain and Northern Ireland (the)" to "GBR",
        "Grenada" to "GRD",
        "Georgia" to "GEO",
        "French Guiana" to "GUF",
        "Guernsey" to "GGY",
        "Ghana" to "GHA",
        "Gibraltar" to "GIB",
        "Greenland" to "GRL",
        "Gambia (the)" to "GMB",
        "Guinea" to "GIN",
        "Guadeloupe" to "GLP",
        "Equatorial Guinea" to "GNQ",
        "Greece" to "GRC",
        "South Georgia and the South Sandwich Islands" to "SGS",
        "Guatemala" to "GTM",
        "Guam" to "GUM",
        "Guinea-Bissau" to "GNB",
        "Guyana" to "GUY",
        "Hong Kong" to "HKG",
        "Heard Island and McDonald Islands" to "HMD",
        "Honduras" to "HND",
        "Croatia" to "HRV",
        "Haiti" to "HTI",
        "Hungary" to "HUN",
        "Indonesia" to "IDN",
        "Ireland" to "IRL",
        "Israel" to "ISR",
        "Isle of Man" to "IMN",
        "India" to "IND ",
        "British Indian Ocean Territory (the)" to "IOT",
        "Iraq" to "IRQ",
        "Iran (Islamic Republic of)" to "IRN",
        "Iceland" to "ISL",
        "Italy" to "ITA",
        "Jersey" to "JEY",
        "Jamaica" to "JAM",
        "Jordan" to "JOR",
        "Japan" to "JPN",
        "Kenya" to "KEN",
        "Kyrgyzstan" to "KGZ",
        "Cambodia" to "KHM",
        "Kiribati" to "KIR",
        "Comoros (the)" to "COM",
        "Saint Kitts and Nevis" to "KNA",
        "Korea (the Democratic People's Republic of)" to "PRK",
        "Korea (the Republic of)" to "KOR",
        "Kuwait" to "KWT",
        "Cayman Islands (the)" to "CYM",
        "Kazakhstan" to "KAZ",
        "Lao People's Democratic Republic (the)" to "LAO",
        "Lebanon" to "LBN",
        "Saint Lucia" to "LCA",
        "Liechtenstein" to "LIE",
        "Sri Lanka" to "LKA",
        "Liberia" to "LBR",
        "Lesotho" to "LSO",
        "Lithuania" to "LTU",
        "Luxembourg" to "LUX",
        "Latvia" to "LVA",
        "Libya" to "LBY",
        "Morocco" to "MAR",
        "Monaco" to "MCO",
        "Moldova (the Republic of)" to "MDA",
        "Montenegro" to "MNE",
        "Saint Martin (French part)" to "MAF",
        "Madagascar" to "MDG",
        "Marshall Islands (the)" to "MHL",
        "North Macedonia" to "MKD",
        "Mali" to "MLI",
        "Myanmar" to "MMR",
        "Mongolia" to "MNG",
        "Macao" to "MAC",
        "Northern Mariana Islands (the)" to "MNP",
        "Martinique" to "MTQ",
        "Mauritania" to "MRT",
        "Montserrat" to "MSR",
        "Malta" to "MLT",
        "Mauritius" to "MUS",
        "Maldives" to "MDV",
        "Malawi" to "MWI",
        "Mexico" to "MEX",
        "Malaysia" to "MYS",
        "Mozambique" to "MOZ",
        "Namibia" to "NAM",
        "New Caledonia" to "NCL",
        "Niger (the)" to "NER",
        "Norfolk Island" to "NFK",
        "Nigeria" to "NGA",
        "Nicaragua" to "NIC",
        "Netherlands (the)" to "NLD",
        "Norway" to "NOR",
        "Nepal" to "NPL",
        "Nauru" to "NRU",
        "Niue" to "NIU",
        "New Zealand" to "NZL",
        "Oman" to "OMN",
        "Panama" to "PAN",
        "Peru" to "PER",
        "French Polynesia" to "PYF",
        "Papua New Guinea" to "PNG",
        "Philippines (the)" to "PHL",
        "Pakistan" to "PAK",
        "Poland" to "POL",
        "Saint Pierre and Miquelon" to "SPM",
        "Pitcairn" to "PCN",
        "Puerto Rico" to "PRI",
        "Palestine, State of" to "PSE",
        "Portugal" to "PRT",
        "Palau" to "PLW",
        "Paraguay" to "PRY",
        "Qatar" to "QAT",
        "Réunion" to "REU",
        "Romania" to "ROU",
        "Serbia" to "SRB",
        "Russian Federation (the)" to "RUS",
        "Rwanda" to "RWA",
        "Saudi Arabia" to "SAU",
        "Solomon Islands" to "SLB",
        "Seychelles" to "SYC",
        "Sudan (the)" to "SDN",
        "Sweden" to "SWE",
        "Singapore" to "SGP",
        "Saint Helena, Ascension and Tristan da Cunha" to "SHN",
        "Slovenia" to "SVN",
        "Svalbard and Jan Mayen" to "SJM",
        "Slovakia" to "SVK",
        "Sierra Leone" to "SLE",
        "San Marino" to "SMR",
        "Senegal" to "SEN",
        "Somalia" to "SOM",
        "Suriname" to "SUR",
        "South Sudan" to "SSD",
        "Sao Tome and Principe" to "STP",
        "El Salvador" to "SLV",
        "Sint Maarten (Dutch part)" to "SXM",
        "Syrian Arab Republic (the)" to "SYR",
        "Eswatini" to "SWZ",
        "Turks and Caicos Islands (the)" to "TCA",
        "Chad" to "TCD",
        "French Southern Territories (the)" to "ATF",
        "Togo" to "TGO",
        "Thailand" to "THA",
        "Tajikistan" to "TJK",
        "Tokelau" to "TKL",
        "Timor-Leste" to "TLS",
        "Turkmenistan" to "TKM",
        "Tunisia" to "TUN",
        "Tonga" to "TON",
        "Türkiye" to "TUR",
        "Trinidad and Tobago" to "TTO",
        "Tuvalu" to "TUV",
        "Taiwan (Province of China)" to "TWN",
        "Tanzania, the United Republic of" to "TZA",
        "Ukraine" to "UKR",
        "Uganda" to "UGA",
        "United States Minor Outlying Islands (the)" to "UMI",
        "United States of America (the)" to "USA",
        "Uruguay" to "URY",
        "Uzbekistan" to "UZB",
        "Holy See (the)" to "VAT",
        "Saint Vincent and the Grenadines" to "VCT",
        "Venezuela (Bolivarian Republic of)" to "VEN",
        "Virgin Islands (British)" to "VGB",
        "Virgin Islands (U.S.)" to "VIR",
        "Viet Nam" to "VNM",
        "Vanuatu" to "VUT",
        "Wallis and Futuna" to "WLF",
        "Samoa" to "WSM",
        "Yemen" to "YEM",
        "Mayotte" to "MYT",
        "South Africa" to "ZAF",
        "Zambia" to "ZMB",
        "Zimbabwe" to "ZWE"
    )
}