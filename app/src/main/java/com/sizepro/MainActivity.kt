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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import kotlin.math.roundToInt

// --- AS 4 CORES DO PROJETO ---
val Azul = Color(0xFF010CFF)
val Roxo = Color(0xFFD600FF)
val Laranja = Color(0xFFFFA400)
val Verde = Color(0xFF0EFF00)
val FundoEscuro = Color(0xFF0F0F11) // Cor de fundo mais profunda
val CardEscuro = Color(0xFF1E1E24) // Cor dos cards
val TecladoBotaoFundo = Color(0xFF2A2A32)

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
            onVoltar = { telaAtual = Tela.HOME },
            onChangeImage = { novoBitmap -> imagemSelecionada = novoBitmap }
        )
    }
}

@Composable
fun TelaHome(onImagemEscolhida: (Bitmap) -> Unit) {
    val context = LocalContext.current
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
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(70.dp).background(Brush.linearGradient(listOf(Roxo, Laranja)), RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("SP", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text("Size Pro", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("Redimensione com qualidade!", fontSize = 14.sp, color = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, Color(0xFF333333), RoundedCornerShape(24.dp))
        ) {
            Image(
                painter = painterResource(id = R.drawable.exemplo), 
                contentDescription = "Exemplo Offline",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp)
                    .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(painter = painterResource(id = android.R.drawable.ic_menu_gallery), contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Exemplo offline", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Text("Escolha uma fotografia", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Selecione uma imagem JPG, PNG ou WebP", fontSize = 14.sp, color = Color.Gray, textAlign = TextAlign.Center)
        
        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth().height(60.dp)
                .background(Brush.linearGradient(listOf(Roxo, Laranja)), RoundedCornerShape(30.dp))
                .clickable { launcher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.AddPhotoAlternate, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Escolher imagem", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = { 
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

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            InfoIcon("🔒", "Seguro", Color(0xFF0F2E1E))
            InfoIcon("⚡", "Rápido", Color(0xFF2E1F0F))
            InfoIcon("⭐", "Qualidade", Color(0xFF2E0F2E))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("• A sua imagem permanece no seu dispositivo", color = Verde, fontSize = 12.sp)
    }
}

@Composable
fun InfoIcon(emoji: String, label: String, bgColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.size(50.dp).background(bgColor, CircleShape), contentAlignment = Alignment.Center) {
            Text(emoji, fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, color = Color.Gray, fontSize = 12.sp)
    }
}

@Composable
fun TelaRedimensionar(imagemOriginal: Bitmap?, onVoltar: () -> Unit, onChangeImage: (Bitmap) -> Unit) {
    val context = LocalContext.current
    
    var larguraStr by remember { mutableStateOf(imagemOriginal?.width?.toString() ?: "0") }
    var alturaStr by remember { mutableStateOf(imagemOriginal?.height?.toString() ?: "0") }
    var isWidthActive by remember { mutableStateOf(true) }
    var isProporcaoBloqueada by remember { mutableStateOf(true) }
    var imagemResultado by remember { mutableStateOf<Bitmap?>(null) }
    var isResultado by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val inputStream = context.contentResolver.openInputStream(it)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            onChangeImage(bitmap)
            larguraStr = bitmap.width.toString()
            alturaStr = bitmap.height.toString()
            isResultado = false
        }
    }

    val tamanhoKB = if (imagemOriginal != null) imagemOriginal.allocationByteCount / 1024 else 0

    Column(modifier = Modifier.fillMaxSize().background(FundoEscuro).padding(16.dp)) {
        
        // TOP BAR
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onVoltar) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = Color.White)
            }
            Text("Resize", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { }) {
                Box(modifier = Modifier.size(36.dp).background(CardEscuro, RoundedCornerShape(10.dp)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.MoreVert, contentDescription = "Mais", tint = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // IMAGE PREVIEW & CHANGE BUTTON
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(180.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(if (isResultado) 2.dp else 1.dp, if (isResultado) Verde else Color(0xFF333333), RoundedCornerShape(20.dp))
                    .background(CardEscuro),
                contentAlignment = Alignment.Center
            ) {
                val bmp = if (isResultado) imagemResultado else imagemOriginal
                bmp?.let {
                    Image(bitmap = it.asImageBitmap(), contentDescription = "Preview", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Button(
                onClick = { launcher.launch("image/*") },
                colors = ButtonDefaults.buttonColors(containerColor = CardEscuro),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(50.dp)
            ) {
                Text("Change", color = Color.White, fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // INFO TEXT
        Text(
            if (isResultado) "Result: $larguraStr x $alturaStr px" else "Original: ${imagemOriginal?.width ?: 0} x ${imagemOriginal?.height ?: 0} px · $tamanhoKB KB",
            color = if (isResultado) Verde else Color.Gray, fontSize = 14.sp, 
            fontWeight = if (isResultado) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // INPUTS
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            InputBox("Width (px)", larguraStr, Azul, isWidthActive) { isWidthActive = true }
            
            IconButton(
                onClick = { isProporcaoBloqueada = !isProporcaoBloqueada },
                modifier = Modifier.padding(horizontal = 8.dp).size(44.dp).background(Roxo, CircleShape)
            ) {
                Icon(
                    imageVector = if (isProporcaoBloqueada) Icons.Filled.Lock else Icons.Filled.LockOpen,
                    contentDescription = "Trava",
                    tint = Color.White
                )
            }

            InputBox("Height (px)", alturaStr, Laranja, !isWidthActive) { isWidthActive = false }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // OUTPUT SIZE BAR
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black, RoundedCornerShape(12.dp))
                .border(1.dp, Color(0xFF333333), RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(if (isResultado) "New size" else "Output size", color = Color.Gray, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(if (isResultado) "$larguraStr x $alturaStr px · ~265 KB" else "—", color = Verde, fontSize = 14.sp, fontWeight = FontWeight.Bold) 
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (!isResultado) {

                        // TECLADO CUSTOMIZADO PREMIUM
            val teclas = listOf(
                listOf("7", "8", "9", "DEL"),
                listOf("4", "5", "6", "CLR"),
                listOf("1", "2", "3", "SWAP"),
                listOf("0", "RESIZE")
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                teclas.forEachIndexed { indexRow, row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        row.forEach { tecla ->
                            if (tecla == "0" && indexRow == 3) {
                                TeclaTeclado(
                                    texto = tecla,
                                    modifier = Modifier.weight(1f),
                                    onClick = { 
                                        if (larguraStr.length < 4 || !isWidthActive) { // Limite de 4 dígitos
                                            adicionarNumero("0", isWidthActive, isProporcaoBloqueada, imagemOriginal, larguraStr, alturaStr) { w, h -> larguraStr = w; alturaStr = h }
                                        }
                                    }
                                )
                            } else if (tecla == "RESIZE") {
                                Button(
                                    onClick = {
                                        val novaLargura = larguraStr.toIntOrNull() ?: imagemOriginal?.width ?: 100
                                        val novaAltura = alturaStr.toIntOrNull() ?: imagemOriginal?.height ?: 100
                                        imagemOriginal?.let {
                                            imagemResultado = Bitmap.createScaledBitmap(it, novaLargura, novaAltura, true)
                                            isResultado = true
                                        }
                                    },
                                    modifier = Modifier.weight(1f).height(60.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = CardEscuro),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Resize", color = Color.Gray, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                TeclaTeclado(
                                    texto = tecla,
                                    modifier = Modifier.weight(1f),
                                    corTexto = when(tecla) { "DEL" -> Azul; "CLR" -> Color.Red; "SWAP" -> Roxo; else -> Color.White },
                                    onClick = {
                                        when (tecla) {
                                            "DEL" -> {
                                                if (isWidthActive) larguraStr = larguraStr.dropLast(1).ifEmpty { "0" }
                                                else alturaStr = alturaStr.dropLast(1).ifEmpty { "0" }
                                            }
                                            "CLR" -> {
                                                if (isWidthActive) larguraStr = "0" else alturaStr = "0"
                                            }
                                            "SWAP" -> {
                                                val temp = larguraStr
                                                larguraStr = alturaStr
                                                alturaStr = temp
                                            }
                                            else -> {
                                                if ((isWidthActive && larguraStr.length < 4) || (!isWidthActive && alturaStr.length < 4)) { // Limite de 4 dígitos
                                                    adicionarNumero(tecla, isWidthActive, isProporcaoBloqueada, imagemOriginal, larguraStr, alturaStr) { w, h -> larguraStr = w; alturaStr = h }
                                                }
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // TELA 3: RESULTADO ULTRA MODERNO
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FormatButton("JPG", isSelected = true, modifier = Modifier.weight(1f))
                FormatButton("PNG", isSelected = false, modifier = Modifier.weight(1f))
                FormatButton("WEBP", isSelected = false, modifier = Modifier.weight(1f))
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = { imagemResultado?.let { salvarECompartilhar(it, context) } }, 
                    modifier = Modifier.weight(1f).height(56.dp), 
                    colors = ButtonDefaults.buttonColors(containerColor = Verde),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Filled.Download, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Download", color = Color.Black, fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = { imagemResultado?.let { salvarECompartilhar(it, context) } }, 
                    modifier = Modifier.weight(1f).height(56.dp), 
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.linearGradient(listOf(Verde, Verde))),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Filled.Share, contentDescription = null, tint = Verde)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Share", color = Verde, fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(onClick = { isResultado = false }, modifier = Modifier.weight(1f).height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = CardEscuro), shape = RoundedCornerShape(12.dp)) { 
                    Text("Resize again", color = Color.White) 
                }
                Button(onClick = onVoltar, modifier = Modifier.weight(1f).height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = CardEscuro), shape = RoundedCornerShape(12.dp)) { 
                    Text("Upload another", color = Color.White) 
                }
            }
        }
    }
}

@Composable
fun InputBox(label: String, value: String, color: Color, isActive: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(12.dp))
            .border(if (isActive) 2.dp else 1.dp, if (isActive) color else Color(0xFF333333), RoundedCornerShape(12.dp))
            .background(if (isActive) CardEscuro else Color.Transparent)
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Column {
            Text(label, color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(
                text = value, 
                color = Color.White, 
                fontSize = 22.sp, 
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis // Impede que o texto quebre o layout
            )
        }
    }
}

@Composable
fun FormatButton(text: String, isSelected: Boolean, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(45.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) Color.Transparent else CardEscuro)
            .border(if (isSelected) 1.dp else 0.dp, if (isSelected) Verde else Color.Transparent, RoundedCornerShape(12.dp))
            .clickable { },
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = if (isSelected) Verde else Color.Gray, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun TeclaTeclado(texto: String, modifier: Modifier = Modifier, corTexto: Color = Color.White, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .height(60.dp)
            .background(TecladoBotaoFundo, RoundedCornerShape(12.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        when (texto) {
            "DEL" -> Icon(Icons.Filled.Backspace, contentDescription = "Apagar", tint = corTexto)
            "CLR" -> Icon(Icons.Filled.Delete, contentDescription = "Limpar", tint = corTexto)
            "SWAP" -> Icon(Icons.Filled.SwapHoriz, contentDescription = "Inverter", tint = corTexto)
            else -> Text(texto, color = corTexto, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// Lógica matemática para manter a proporção e limitar a 4 dígitos!
fun adicionarNumero(
    num: String, isWidthActive: Boolean, isLocked: Boolean, imagemOriginal: Bitmap?, 
    currentLargura: String, currentAltura: String, onUpdate: (String, String) -> Unit
) {
    val ratio = if (imagemOriginal != null && imagemOriginal.width > 0) {
        imagemOriginal.height.toFloat() / imagemOriginal.width.toFloat()
    } else 1f

    if (isWidthActive) {
        if (currentLargura.length >= 4) return // BLOQUEIO DE 4 DÍGITOS
        val novaLarguraStr = if (currentLargura == "0") num else currentLargura + num
        val novaLarguraInt = novaLarguraStr.toIntOrNull() ?: 0
        
        if (isLocked) {
            val novaAlturaInt = (novaLarguraInt * ratio).roundToInt()
            onUpdate(novaLarguraStr, novaAlturaInt.toString())
        } else {
            onUpdate(novaLarguraStr, currentAltura)
        }
    } else {
        if (currentAltura.length >= 4) return // BLOQUEIO DE 4 DÍGITOS
        val novaAlturaStr = if (currentAltura == "0") num else currentAltura + num
        val novaAlturaInt = novaAlturaStr.toIntOrNull() ?: 0
        
        if (isLocked && ratio > 0) {
            val novaLarguraInt = (novaAlturaInt / ratio).roundToInt()
            onUpdate(novaLarguraInt.toString(), novaAlturaStr)
        } else {
            onUpdate(currentLargura, novaAlturaStr)
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
