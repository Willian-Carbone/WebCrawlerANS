package Metodos


import groovyx.net.http.FromServer
import groovyx.net.http.HttpBuilder
import jakarta.mail.internet.*
import jakarta.mail.*
import org.jsoup.select.Elements
import static groovyx.net.http.HttpBuilder.configure
import java.util.zip.*
import java.nio.file.*


class Utilitarios {

    static boolean validadorEmail(String email) {
        return (email ==~ /[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}/)
    }

    static boolean validarEntrada(ArrayList<String> itens, String entrada) {
        return itens.contains(entrada)
    }


    static void baixarArquivo(String url, String nomeArquivo, String local) {
        Path raizDoProjeto = Paths.get("").toAbsolutePath()
        Path pastaDestino = raizDoProjeto.resolve("../../../").resolve(local)

        Path arquivoCompleto = pastaDestino.resolve(nomeArquivo)


        if (!Files.exists(pastaDestino)) {
            Files.createDirectories(pastaDestino)
        }


        HttpBuilder htpp = configure {
            request.uri = url
        }


        htpp.get {

            response.parser('*/*') { config, resp ->
                return resp.inputStream
            }

            response.success { FromServer fs ->

                Files.copy(fs.inputStream, arquivoCompleto, StandardCopyOption.REPLACE_EXISTING)

            }


        }


    }


    static void criadorCsv(Elements linhasDaTabela, String nomeArquivo, String local) {
        Path raizDoProjeto = Paths.get("").toAbsolutePath()
        Path pastaDestino = raizDoProjeto.resolve("../../../").resolve(local)

        Path arquivoCompleto = pastaDestino.resolve(nomeArquivo + ".csv")

        File arquivoCsv = arquivoCompleto.toFile()

        if (!Files.exists(pastaDestino)) {
            Files.createDirectories(pastaDestino)
        }

        arquivoCsv.withWriter("UTF-8") { writer ->
            linhasDaTabela.each { tr ->

                Elements celulas = tr.select("th, td")

                String linhaFormatada = celulas.collect { celula ->
                    String texto = celula.text()
                            .replaceAll("\\s+", " ")
                            .replace(";", ",")
                            .trim()

                    return "\"${texto}\""


                }.join(";")

                writer.writeLine(linhaFormatada)
            }
        }


    }

    static File ziparPasta(Path pastaOrigem, Path destino=null) {

        Path caminhoDestino

        if(destino){
            caminhoDestino = destino.resolve("pacote_completo_ans.zip")
        }

        else{
            caminhoDestino = Paths.get("").toAbsolutePath().resolve("../../../pacote_completo_ans.zip")

        }


        File arquivoZipFinal = caminhoDestino.toFile()


        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(arquivoZipFinal))


        Files.walk(pastaOrigem).forEach { Path caminho ->

            String caminhoString = caminho.toString()


            if (caminhoString.contains(File.separator + "rep.ConceitoSaude"+ File.separator) ||
                    caminhoString.endsWith(File.separator + "rep.ConceitoSaude")) {
                return
            }

            String nomeNoZip = pastaOrigem.relativize(caminho).toString()

            if (Files.isDirectory(caminho)) {
                if (!nomeNoZip.isEmpty()) {
                    zos.putNextEntry(new ZipEntry(nomeNoZip + "/"))
                    zos.closeEntry()
                }
            } else {
                ZipEntry entry = new ZipEntry(nomeNoZip)
                zos.putNextEntry(entry)

                Files.copy(caminho, zos)
                zos.closeEntry()
            }
        }
        zos.close()

        return arquivoZipFinal

    }



    static boolean enviarParaInteressado(String emailDestino, File arquivoZip) {
        String emailUsuario = System.getenv("EMAIL_USER")
        String senhaApp = System.getenv("EMAIL_PASS")

        if (emailUsuario == null || senhaApp == null) {
            return false
        }

        Properties props = new Properties()
        props.put("mail.smtp.host", "smtp.gmail.com")
        props.put("mail.smtp.port", "587")
        props.put("mail.smtp.auth", "true")
        props.put("mail.smtp.starttls.enable", "true")

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailUsuario, senhaApp)
            }
        })

        try {
            Message message = new MimeMessage(session)
            message.setFrom(new InternetAddress(emailUsuario))
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailDestino))
            message.setSubject("Relatório ANS - Arquivos Baixados")

            MimeBodyPart parteAnexo = new MimeBodyPart()
            parteAnexo.attachFile(arquivoZip)

            Multipart multipart = new MimeMultipart()
            multipart.addBodyPart(parteAnexo)

            message.setContent(multipart)

            Transport.send(message)
            return true

        } catch (Exception ignored) {
            return false
        }
    }
    }


