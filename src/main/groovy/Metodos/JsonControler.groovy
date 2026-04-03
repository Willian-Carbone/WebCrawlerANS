package Metodos

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper

import java.nio.file.Path

class JsonControler {

    static void salvarEmails(Map<String, String> mapaEmails, Path local = null ) {


        File arquivo

        if (local) {

            arquivo = local.resolve("emails.json").toFile()
        } else {

            String raizDoProjeto = "../../../"
            arquivo = new File(raizDoProjeto + "emails.json")
        }



        arquivo.withWriter("UTF-8") { writer ->
            writer.write(new JsonBuilder(mapaEmails).toPrettyString())
        }
    }

    static Map capturarEmails(Path local=null){

        File arquivo

        if (local) {

            arquivo = local.resolve("emails.json").toFile()
        } else {
            arquivo = new File("../../../emails.json")
        }

        if (!arquivo.exists()) {
            return [:]

        }

        Map emails = new JsonSlurper().parse(arquivo)

        return  emails
    }
}
