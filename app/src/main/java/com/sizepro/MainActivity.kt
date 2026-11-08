package com.sizepro

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

// --- AS 4 CORES DO PROJETO ---
val Azul = Color(0xFF010CFF)
val Roxo = Color(0xFFD600FF)
val Laranja = Color(0xFFFFA400)
val Verde = Color(0xFF0EFF00)
val FundoEscuro = Color(0xFF121212)
val CardEscuro = Color(0xFF242424)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                SizeProApp()
            }
        }
    }
}

enum class Tela { HOME, REDIMENSIONAR }

@Composable
fun SizeProApp() {
    var telaAtual by remember { mutableStateOf(Tela.HOME) }
    var imagemSelecionada by remember { mutableStateOf<Bitmap?>(null) }

    when (telaAtual) {
        Tela.HOME -> TelaHome(
            onImagemEscolhida = { bitmap ->
                imagemSelecionada = bitmap
                telaAtual = Tela.REDIMENSIONAR
            }
        )
        Tela.REDIMENSIONAR -> TelaRedimensionar(
            imagemOriginal = imagemSelecionada,
            onVoltar = { telaAtual = Tela.HOME }
        )
    }
}

@Composable
fun TelaHome(onImagemEscolhida: (Bitmap) -> Unit) {
    val context = LocalContext.current
    
    // Launcher para escolher imagem da galeria
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val inputStream = context.contentResolver.openInputStream(it)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            onImagemEscolhida(bitmap)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(FundoEscuro).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        
        // Logo
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(70.dp).background(Brush.linearGradient(listOf(Roxo, Laranja)), RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("SP", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text("Size Pro", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("Redimensione com qualidade!", fontSize = 14.sp, color = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- CARD DA IMAGEM DE EXEMPLO (Igual à Imagem 1) ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, Color.DarkGray, RoundedCornerShape(16.dp))
        ) {
            // Carrega a imagem da pasta res/drawable
            // Se o seu ficheiro for png, mude para R.drawable.exemplo (mantenha o nome exemplo)
            Image(
                painter = painterResource(id = R.drawable.exemplo), 
                contentDescription = "Exemplo Offline",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            
            // Etiqueta "Exemplo offline"
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp)
                    .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_menu_gallery), 
                        contentDescription = null, 
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Exemplo offline", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Escolha uma fotografia", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Selecione uma imagem JPG, PNG ou WebP", fontSize = 14.sp, color = Color.Gray, textAlign = TextAlign.Center)
        
        Spacer(modifier = Modifier.height(24.dp))

        // Botão Escolher Imagem
        Box(
            modifier = Modifier
                .fillMaxWidth().height(60.dp)
                .background(Brush.linearGradient(listOf(Roxo, Laranja)), RoundedCornerShape(30.dp))
                .clickable { launcher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            Text("Escolher imagem", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botão Usar Imagem de Exemplo
        OutlinedButton(
            onClick = { 
                // Pega a imagem da pasta drawable e converte para Bitmap para podermos redimensionar
                val bitmapExemplo = BitmapFactory.decodeResource(context.resources, R.drawable.exemplo)
                onImagemEscolhida(bitmapExemplo)
            },
            modifier = Modifier.fillMaxWidth().height(60.dp),
            border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.linearGradient(listOf(Azul, Azul))),
            shape = RoundedCornerShape(30.dp)
        ) {
            Icon(Icons.Filled.Star, contentDescription = null, tint = Roxo)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Usar imagem de exemplo", color = Color.White, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.weight(1f))

        // Rodapé
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier.size(50.dp).background(Color(0xFF0F2E1E), CircleShape), contentAlignment = Alignment.Center) {
                    Text("🔒", fontSize = 20.sp)
                }
                Text("Seguro", color = Color.Gray, fontSize = 12.sp)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier.size(50.dp).background(Color(0xFF2E1F0F), CircleShape), contentAlignment = Alignment.Center) {
                    Text("⚡", fontSize = 20.sp)
                }
                Text("Rápido", color = Color.Gray, fontSize = 12.sp)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier.size(50.dp).background(Color(0xFF2E0F2E), CircleShape), contentAlignment = Alignment.Center) {
                    Text("⭐", fontSize = 20.sp)
                }
                Text("Qualidade", color = Color.Gray, fontSize = 12.sp)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("• A sua imagem permanece no seu dispositivo", color = Verde, fontSize = 12.sp)
    }
}

@Composable
fun TelaRedimensionar(imagemOriginal: Bitmap?, onVoltar: () -> Unit) {
    val context = LocalContext.current
    var larguraStr by remember { mutableStateOf(if (imagemOriginal != null) imagemOriginal.width.toString() else "0") }
    var alturaStr by remember { mutableStateOf(if (imagemOriginal != null) imagemOriginal.height.toString() else "0") }
    
    var imagemResultado by remember { mutableStateOf<Bitmap?>(null) }
    var isResultado by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().background(FundoEscuro).padding(16.dp)) {
        
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onVoltar) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Voltar", tint = Color.White)
            }
            Text("Resize", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth().height(200.dp)
                .border(if (isResultado) 2.dp else 0.dp, if (isResultado) Verde else Color.Transparent, RoundedCornerShape(16.dp))
                .background(CardEscuro, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            val imagemParaMostrar = if (isResultado) imagemResultado else imagemOriginal
            if (imagemParaMostrar != null) {
                Image(
                    bitmap = imagemParaMostrar.asImageBitmap(),
                    contentDescription = "Preview",
                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text("Nenhuma imagem", color = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        val infoText = if (isResultado) "Result: ${larguraStr} x ${alturaStr} px" else "Original: ${imagemOriginal?.width} x ${imagemOriginal?.height} px"
        Text(infoText, color = if (isResultado) Verde else Color.Gray, fontSize = 14.sp, modifier = Modifier.align(Alignment.CenterHorizontally))

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(
                value = larguraStr,
                onValueChange = { larguraStr = it },
                label = { Text("Width (px)", color = Azul) },
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Azul, unfocusedBorderColor = Color.Gray, focusedTextColor = Color.White)
            )
            OutlinedTextField(
                value = alturaStr,
                onValueChange = { alturaStr = it },
                label = { Text("Height (px)", color = Laranja) },
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Laranja, unfocusedBorderColor = Color.Gray, focusedTextColor = Color.White)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (!isResultado) {
            Button(
                onClick = {
                    val novaLargura = larguraStr.toIntOrNull() ?: imagemOriginal?.width ?: 100
                    val novaAltura = alturaStr.toIntOrNull() ?: imagemOriginal?.height ?: 100
                    imagemOriginal?.let {
                        imagemResultado = Bitmap.createScaledBitmap(it, novaLargura, novaAltura, true)
                        isResultado = true
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Roxo),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Resize", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        } else {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {}, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = CardEscuro), border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.linearGradient(listOf(Verde, Verde)))) { Text("JPG", color = Verde) }
                Button(onClick = {}, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = CardEscuro)) { Text("PNG", color = Color.Gray) }
                Button(onClick = {}, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = CardEscuro)) { Text("WEBP", color = Color.Gray) }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = { imagemResultado?.let { salvarECompartilhar(it, context) } }, 
                    modifier = Modifier.weight(1f), 
                    colors = ButtonDefaults.buttonColors(containerColor = Verde)
                ) {
                    Text("⬇ Download", color = Color.Black, fontWeight = FontWeight.Bold)
                }
                OutlinedButton(onClick = {}, modifier = Modifier.weight(1f), border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.linearGradient(listOf(Verde, Verde)))) {
                    Text("⬆ Share", color = Verde)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            Box(modifier = Modifier.fillMaxWidth().height(60.dp).background(Color.Black).border(1.dp, Azul, RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                Text("Ads banner", color = Color.Gray)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(onClick = { isResultado = false }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = CardEscuro)) { Text("Resize again", color = Color.White) }
                Button(onClick = onVoltar, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = CardEscuro)) { Text("Upload another", color = Color.White) }
            }
        }
    }
}

fun salvarECompartilhar(bitmap: Bitmap, context: Context) {
    try {
        val cachePath = File(context.cacheDir, "images")
        cachePath.mkdirs()
        val file = File(cachePath, "shared_image.png")
        val stream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        stream.close()

        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Partilhar Imagem"))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
