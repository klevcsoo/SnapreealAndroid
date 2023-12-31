package com.klevcsoo.snapreealandroid.media

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.ReturnCode
import java.io.File

fun generateThumbnailFor(file: File): File {
    val outPath = "${file.absolutePath}.jpg"
    val outFile = File(outPath)
    if (outFile.exists()) outFile.delete()

    val cmd = "-i ${file.absolutePath} -vframes 1 -q:v 2 $outPath"
    val session = FFmpegKit.execute(cmd)
    if (ReturnCode.isSuccess(session.returnCode)) {
        return outFile
    } else if (ReturnCode.isCancel(session.returnCode)) {
        throw Error("FFMpeg session is cancelled.")
    } else {
        throw Error("FFMpeg session exited with an error: ${session.failStackTrace}")
    }
}

fun calculateIsThumbnailDark(file: File): Boolean {
    val pixelSpacing = 1
    val bitmap = BitmapFactory.decodeFile(file.absolutePath)

    var r = 0
    var g = 0
    var b = 0
    val height = bitmap.height
    val width = bitmap.width
    val pixels = IntArray(width * height)
    bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
    var i = 0

    while (i < pixels.size) {
        val color = pixels[i]
        r += Color.red(color)
        g += Color.green(color)
        b += Color.blue(color)
        i += pixelSpacing
    }

    return (r + b + g) / (pixels.size * 3) < 160
}

fun concatVideoFiles(context: Context, videos: List<File>): File {
    val workDir = File(context.filesDir, "diary_render")
    if (workDir.exists()) workDir.deleteRecursively()
    workDir.mkdir()

    val videoRepoFile = File(workDir.absolutePath, "video_repo.txt")
    videoRepoFile.printWriter().use { out ->
        videos.forEach { out.println("file '${it.absolutePath}'") }
    }

    val outFile = File(workDir.absolutePath, "generated.mp4")
    val cmd = "-f concat -safe 0 -i ${videoRepoFile.absolutePath} -c copy ${outFile.absolutePath}"
    val session = FFmpegKit.execute(cmd)
    if (ReturnCode.isSuccess(session.returnCode)) {
        return outFile
    } else if (ReturnCode.isCancel(session.returnCode)) {
        throw Error("FFMpeg session is cancelled.")
    } else {
        throw Error("FFMpeg session exited with an error: ${session.failStackTrace}")
    }
}
