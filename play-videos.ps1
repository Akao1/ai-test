param(
    [Parameter(Position=0)]
    [string]$Path = ".",
    [string]$Player = "",          # auto/mpv/vlc/ffplay
    [int]$MaxRetries = 2,
    [int]$TimeoutSec = 10,         # seconds to wait before considering file stuck
    [switch]$Shuffle,
    [switch]$Loop,
    [string[]]$Extensions = @("*.mp4", "*.mkv", "*.avi", "*.mov", "*.wmv", "*.flv", "*.webm", "*.ts", "*.m4v", "*.3gp", "*.ogv")
)

function Find-MediaPlayer {
    $players = @(
        @{ Name="mpv";    Cmd="mpv";    Test={ Get-Command mpv -ErrorAction SilentlyContinue } }
        @{ Name="vlc";    Cmd="vlc";    Test={ Get-Command vlc -ErrorAction SilentlyContinue } }
        @{ Name="ffplay"; Cmd="ffplay"; Test={ Get-Command ffplay -ErrorAction SilentlyContinue } }
    )

    if ($Player -and $Player -ne "auto") {
        $p = $players | Where-Object { $_.Name -eq $Player }
        if ($p -and & $p.Test) { return $p }
        Write-Warning "Player '$Player' not found, auto-detecting..."
    }

    foreach ($p in $players) {
        if (& $p.Test) { return $p }
    }
    return $null
}

function Get-PlayerArgs {
    param([string]$PlayerName, [string]$File)
    switch ($PlayerName) {
        "mpv"    { return @("--keep-open=no", "--term-status-msg=", "--no-resume-playback", "--", $File) }
        "vlc"    { return @("--play-and-exit", "--no-repeat", "--intf", "dummy", "--", $File) }
        "ffplay" { return @("-autoexit", "-loglevel", "quiet", "-nodisp", $File) }
    }
}

function Test-PlayerRunning {
    param([string]$Name)
    $procs = Get-Process -Name $Name -ErrorAction SilentlyContinue
    return ($procs.Count -gt 0)
}

# --- main ---
$player = Find-MediaPlayer
if (-not $player) {
    Write-Host "No supported media player found. Install one:" -ForegroundColor Yellow
    Write-Host "  mpv (recommended):  winget install mpv" -ForegroundColor Cyan
    Write-Host "  VLC:               winget install vlc" -ForegroundColor Cyan
    Write-Host "  FFmpeg (ffplay):   winget install ffmpeg" -ForegroundColor Cyan
    exit 1
}

$resolved = Resolve-Path $Path -ErrorAction Stop
$items = if (Test-Path -LiteralPath $resolved -PathType Container) {
    $extPatterns = $Extensions | ForEach-Object { "`"$_`"" }
    $filter = $Extensions -join ", "
    Get-ChildItem -LiteralPath $resolved -Recurse -Include $Extensions -File | Sort-Object FullName
} else {
    Get-Item -LiteralPath $resolved
}

if (-not $items) {
    Write-Host "No video files found in: $resolved" -ForegroundColor Yellow
    exit 0
}

if ($Shuffle) {
    $items = $items | Sort-Object { Get-Random }
}

$total = $items.Count
$idx = 0
$failCount = 0

do {
    foreach ($file in $items) {
        $idx++
        $name = $file.Name
        $size = "{0:N1} MB" -f ($file.Length / 1MB)
        Write-Host ""
        Write-Host ("=" * 60) -ForegroundColor DarkGray
        Write-Host "[$idx/$total] Playing: $name  ($size)" -ForegroundColor Green
        Write-Host ("=" * 60) -ForegroundColor DarkGray

        $args = Get-PlayerArgs $player.Name $file.FullName
        $ok = $false

        for ($try = 0; $try -le $MaxRetries; $try++) {
            if ($try -gt 0) {
                Write-Host "  Retry $try/$MaxRetries ..." -ForegroundColor Yellow
            }

            $proc = Start-Process -FilePath $player.Cmd -ArgumentList $args -NoNewWindow -Wait -PassThru

            if ($proc.ExitCode -eq 0) {
                $ok = $true
                break
            } elseif ($proc.ExitCode -eq 1 -and $player.Name -eq "mpv") {
                # mpv exit code 1 = error
                Write-Warning "  Player error (exit code $($proc.ExitCode)), skipping..."
                break  # skip on error, no retry for player error
            } else {
                Write-Warning "  Exit code $($proc.ExitCode), retrying..."
            }

            Start-Sleep -Milliseconds 500
        }

        if (-not $ok) {
            $failCount++
            Write-Host "  >> Skipped: $name" -ForegroundColor Red
        }
    }

    $total = $items.Count
    if ($Loop) {
        $idx = 0
        Write-Host "`n--- Restarting playlist (Loop mode) ---" -ForegroundColor Cyan
    }
} while ($Loop)

Write-Host "`nDone! Played $total files, $failCount skipped." -ForegroundColor Green
