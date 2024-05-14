package br.com.imoveisservlet.servelet;
import br.com.imoveisservlet.dao.CadastroImovelDao;
import br.com.imoveisservlet.model.CadastroImovel;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload.isMultipartContent;


@WebServlet("/cadastroImovel")

public class CreateCadastroImovel extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {




        Map<String, String> parameters = uploadImage(req);



        String idCadastroImovel = parameters.get("idCadastroImovel");
        String titulo_imovel = parameters.get("titulo-imovel");
        String endereco = parameters.get("endereco");
        String num_quartos = parameters.get("numero-quartos");
        String num_banheiro = parameters.get("numero-banheiros");
        String num_vagas = parameters.get("numero-vagas");
        String valor_noite = parameters.get("valor");
        String imagens = parameters.get("imagens");
        String obs = parameters.get("obs");
        String email = parameters.get("email-contato");
        String telefone = parameters.get("telefone-contato");

        CadastroImovelDao cadastroImovelDao = new CadastroImovelDao();
        CadastroImovel cadastroImovel = new CadastroImovel(idCadastroImovel, titulo_imovel, endereco, num_quartos, num_banheiro, num_vagas, valor_noite, imagens, obs,email,telefone);


        if (idCadastroImovel != null && !idCadastroImovel.isBlank()) {
            cadastroImovelDao.updateCadastroImovel(cadastroImovel);
        } else {

            cadastroImovelDao.createImovel(cadastroImovel);
        }


        resp.sendRedirect("/HomeLogada");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("cadastroImovel.jsp").forward(req, resp);

    }

    private Map<String, String> uploadImage(HttpServletRequest request) {
        HashMap<String, String> parameters = new HashMap<>();

        try {
            DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();
            List<FileItem> fileItemList = new ServletFileUpload(diskFileItemFactory).parseRequest(request);

            for (FileItem fileItem : fileItemList) {
                if (fileItem.isFormField()) {
                    parameters.put(fileItem.getFieldName(), fileItem.getString());
                } else {
                    String fileName = processUploadedFile(fileItem);
                    parameters.put("imagens", fileName);
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // Adicione o tratamento de exceção apropriado
            parameters.put("imagens", "Imagens/logo.png");
        }

        return parameters;
    }
    private void checkFieldType(FileItem files, Map requestParameter)throws Exception{
        if (files.isFormField()){
            requestParameter.put(files.getFieldName(), files.getString());
        }else {
            String fileName =   processUploadedFile(files);
            requestParameter.put("image", fileName);

        }

    }

    private String processUploadedFile(FileItem files)throws Exception {
        Long time =  new Date().getTime();
        String fileName = time.toString().concat("-").concat(files.getName().replace(" ", ""));
        String filePath = this.getServletContext().getRealPath("img").concat(File.separator).concat(fileName);
        files.write(new File(filePath));
        return fileName;

    }





}