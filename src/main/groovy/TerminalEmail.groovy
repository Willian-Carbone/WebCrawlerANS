import Metodos.JsonControler
import Metodos.Utilitarios

class TerminalEmail {

    static Map atualizarDados(){

        Map <String,String> emails= JsonControler.capturarEmails()
        if(emails.size()==1){
            println("não há emais salvos")
        }

        else{
            println("Os seguintes emails foram cadastrados")
            emails.each {identificador,email->
                if(identificador=="proximoId"){return }

                println("identificador: ${identificador} |   email : ${email}")

            }
        }


        println(" 1 para adicionar, 2 para editar , 3 para apagar ou 4 para sair")

        return  emails

    }


    static void terminal(Scanner c){


        Map emails = atualizarDados()
        String entrada = c.nextLine()

        while(entrada!="4"){
            boolean confirmacao = false


            while(!Utilitarios.validarEntrada(["1","2","3"],entrada)){
                println("Insira um valor valido")
                entrada=c.nextLine()
            }

            if(entrada=="1"){
                println("Informe o email ou digite sair ")
                String nomeEmail =  c.nextLine()

                while(!Utilitarios.validadorEmail(nomeEmail) && nomeEmail!="sair")
                {
                    println("Informe um email valido ou cancele a operação")
                    nomeEmail=c.nextLine()

                }

                if(nomeEmail!="sair"){
                    emails[emails["proximoId"] as String] = nomeEmail
                    emails["proximoId"]+=1
                    confirmacao =true
                    JsonControler.salvarEmails(emails)

                }


            }

            if(entrada=="2"){
                println("Insira o identificador da tarefa para edita-la ou sair para cancelar a ação" )
                String identificadorEmail = c.nextLine()

                while(identificadorEmail!="sair" && !emails.containsKey(identificadorEmail)){
                    println("O identificador nao foi encontrado, insira um identificador valido ou digite sair")
                    identificadorEmail = c.nextLine()
                }

                if(identificadorEmail!="sair"){
                    println("email encontrado  ${emails[entrada]}, digite o novo email ou sair para cancelar ação")
                    String novoEmail= c.nextLine()

                    while (!Utilitarios.validadorEmail(novoEmail) && novoEmail!="sair"){
                        println("Insira um novo email valido ou cancele a ação")

                    }

                    if(novoEmail!="sair"){
                        emails[identificadorEmail] = novoEmail
                        JsonControler.salvarEmails(emails)
                        confirmacao = true
                    }
                }



            }

            if(entrada=="3"){
                println("Insira o identificador do email a ser removido ou sair para cancelar a ação")
                String identificadorEmail = c.nextLine()

                while(identificadorEmail!="sair" && !emails.containsKey(identificadorEmail)){
                    println("O identificador nao foi encontrado, insira um identificador valido ou digite sair")
                    identificadorEmail = c.nextLine()
                }

                if(identificadorEmail!="sair"){
                    emails.remove(identificadorEmail)
                    confirmacao = true
                    JsonControler.salvarEmails(emails)
                }
            }


            if(confirmacao) {println("Ação realizada com sucesso")}
            else{println("Ação cancelada")}

            emails = atualizarDados()
             entrada = c.nextLine()



        }





    }
}
