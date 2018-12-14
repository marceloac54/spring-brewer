package com.marcelo.brewer.storage.local;

import static java.nio.file.FileSystems.getDefault;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.marcelo.brewer.storage.FotoStorage;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.name.Rename;

@Profile("local")
@Component
public class FotoStorageLocal implements FotoStorage {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FotoStorageLocal.class);
	private static final String THUMBNAIL_PREFIX = "thumbnail.";
	
	private Path local;
	
	public FotoStorageLocal() {
		this(getDefault().getPath(System.getenv("USERNAME"), ".brewerfotos"));
	}
	
	public FotoStorageLocal(Path path) {
		this.local = path;
		criarPastas();
	}
	

	@Override
	public String salvar(MultipartFile[] files) {
		String novoNome = null;
		if (files!= null && files.length > 0) {
			MultipartFile arquivo = files[0];
			novoNome = renomearArquivo( arquivo.getOriginalFilename());
			try {
				arquivo.transferTo(new File(this.local.toAbsolutePath().toString() + getDefault().getSeparator() + 
						novoNome ));
			} catch (IOException e) {
				throw new RuntimeException("Erro salvando foto", e);
			}
		}
		
		try {
			Thumbnails.of(this.local.resolve(novoNome).toString()).size(40, 68).toFiles(Rename.PREFIX_DOT_THUMBNAIL);
		} catch (IOException e) {
			throw new RuntimeException("Erro gerando thumbnail", e);
		};

		return novoNome;
	}
	
	
	@Override
	public byte[] recuperar(String foto) {
		try {
			return Files.readAllBytes(this.local.resolve(foto));
		} catch (IOException e) {
			throw new RuntimeException("Erro lendo a foto destino final", e);
		}
	}

	private void criarPastas() {
		try {
			Files.createDirectories(this.local);
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Pastas criadas para armazenar fotos");
				LOGGER.debug("Pasta default: " + this.local.toAbsolutePath());
				LOGGER.debug("Pasta temporaria: " + this.local.toAbsolutePath());
			}
			
		} catch (IOException e) {
			throw new RuntimeException("Erro criando pasta para salvar foto", e);
		}
	}
	
//	private String renomearArquivo(String nomeOriginal) {
//		String novoNome = UUID.randomUUID().toString() + "_" + nomeOriginal;
//		
//		if(LOGGER.isDebugEnabled()) {
//			LOGGER.debug(String.format("Nome original: %s, novo nome: %s", nomeOriginal, novoNome));
//		}
//		
//		return novoNome;
//	}

	@Override
	public byte[] recuperarThumbnail(String fotoCerveja) {
		return recuperar(THUMBNAIL_PREFIX + fotoCerveja);
	}

	@Override
	public void excluir(String foto) {
		try {
			Files.deleteIfExists(this.local.resolve(foto));
			Files.deleteIfExists(this.local.resolve(THUMBNAIL_PREFIX + foto));
		} catch (IOException e) {
			LOGGER.warn(String.format("Erro apagando foto '%s'. Mensagem: %s", foto, e.getMessage()));
		}
		
	}

	@Override
	public String getUrl(String nomeFoto) {
		return "http://localhost:8080/brewer/fotos/" + nomeFoto;
	}

}
