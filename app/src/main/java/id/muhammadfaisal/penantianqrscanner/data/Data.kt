package id.muhammadfaisal.penantianqrscanner.data

import id.muhammadfaisal.penantianqrscanner.R
import id.muhammadfaisal.penantianqrscanner.model.Language

class Data {
    companion object{
        fun pin() : ArrayList<String> {
            val pins = ArrayList<String>()
            pins.apply {
                add("846751")
                add("221309")
                add("939320")
                add("091322")
                add("092213")
                add("220913")
                add("130922")
                add("281219")
                add("091220")
                add("220639")
                add("130839")
                add("220622")
                add("131309")
                add("818179")
                add("919184")
            }

            return pins
        }

        fun language() : ArrayList<Language>{
            val languages = ArrayList<Language>()
            languages.add(Language(R.drawable.indonesia, "Indonesia"))
            languages.add(Language(R.drawable.inggris, "English"))

            return languages
        }
    }
}