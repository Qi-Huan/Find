package com.qihuan.moviemodule.model.bean

import java.io.Serializable

class PersonBean : Serializable {
    var avatars: ImagesBean = ImagesBean()
    var name: String = ""
    var id: String = ""

    var mobile_url: String? = null
    var gender: String? = null
    var name_en: String? = null
    var born_place: String? = null
    var alt: String? = null
    var aka_en: List<String>? = null
    var works: List<WorksBean>? = null
    var aka: List<String>? = null

    var isDirector = false
}
