package Metodos

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper

class JsonControler {

    static void salvarEmails(Map<String, String> mapaEmails ) {



        String raizDoProjeto = "../../../"

        File arquivo = new File((raizDoProjeto as String)+"emails.json")



        arquivo.withWriter("UTF-8") { writer ->
            writer.write(new JsonBuilder(mapaEmails).toPrettyString())
        }
    }

    static Map capturarEmails(){

        String raizDoProjeto = "../../../"

        File arquivo = new File((raizDoProjeto as String)+"emails.json")

        if (!arquivo.exists()) {
            return [:]

        }

        Map emails = new JsonSlurper().parse(arquivo)

        return  emails
    }
}
