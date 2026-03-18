package com.netah.hakkam.numyah.mind.mapper

interface Mapper<FROM, TO>:MapperData<FROM, TO>, MapperDomain<FROM, TO>

interface MapperData<FROM, TO> {
    fun fromDataToDomain(table: FROM): TO
}
interface MapperDomain<FROM, TO> {
    fun fromDomainToData(table: FROM): TO
}