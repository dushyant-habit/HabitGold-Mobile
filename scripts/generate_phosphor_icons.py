#!/usr/bin/env python3

from __future__ import annotations

import argparse
import xml.etree.ElementTree as ET
from dataclasses import dataclass
from pathlib import Path


REPO_ROOT = Path(__file__).resolve().parent.parent
DEFAULT_SOURCE_ROOT = Path("/tmp/core-main/assets")
DEFAULT_OUTPUT = (
    REPO_ROOT
    / "composeApp/src/commonMain/kotlin/com/habit/gold/core/designsystem/icons/HabitGoldPhosphorIcons.kt"
)


@dataclass(frozen=True)
class IconSpec:
    group_name: str
    asset_name: str
    kotlin_name: str
    weight: str = "regular"
    auto_mirror: bool = False


@dataclass(frozen=True)
class PathSpec:
    data: str
    fill_type: str
    fill_alpha: float


ICON_SPECS = [
    IconSpec("Regular", "arrows-clockwise", "ArrowsClockwise"),
    IconSpec("Regular", "arrow-down", "ArrowDown"),
    IconSpec("Regular", "arrow-up", "ArrowUp"),
    IconSpec("Regular", "bank", "Bank"),
    IconSpec("Regular", "bell", "Bell"),
    IconSpec("Regular", "calculator", "Calculator"),
    IconSpec("Regular", "calendar-blank", "CalendarBlank"),
    IconSpec("Regular", "caret-down", "CaretDown"),
    IconSpec("Regular", "caret-left", "CaretLeft"),
    IconSpec("Regular", "caret-right", "CaretRight"),
    IconSpec("Regular", "caret-up", "CaretUp"),
    IconSpec("Regular", "chat-circle-dots", "ChatCircleDots"),
    IconSpec("Regular", "check", "Check"),
    IconSpec("Regular", "check-circle", "CheckCircle"),
    IconSpec("Regular", "circle", "Circle"),
    IconSpec("Regular", "clock", "Clock"),
    IconSpec("Regular", "clock-counter-clockwise", "ClockCounterClockwise"),
    IconSpec("Regular", "coins", "Coins"),
    IconSpec("Regular", "copy", "Copy"),
    IconSpec("Regular", "credit-card", "CreditCard"),
    IconSpec("Regular", "currency-inr", "CurrencyInr"),
    IconSpec("Regular", "download-simple", "DownloadSimple"),
    IconSpec("Regular", "envelope", "Envelope"),
    IconSpec("Regular", "eye", "Eye"),
    IconSpec("Regular", "eye-slash", "EyeSlash"),
    IconSpec("Regular", "file-text", "FileText"),
    IconSpec("Regular", "fingerprint", "Fingerprint"),
    IconSpec("Regular", "gift", "Gift"),
    IconSpec("Regular", "hand-coins", "HandCoins"),
    IconSpec("Regular", "headset", "Headset"),
    IconSpec("Regular", "heart", "Heart"),
    IconSpec("Regular", "house", "House"),
    IconSpec("Regular", "house-line", "HouseLine"),
    IconSpec("Regular", "identification-badge", "IdentificationBadge"),
    IconSpec("Regular", "info", "Info"),
    IconSpec("Regular", "lightbulb", "Lightbulb"),
    IconSpec("Regular", "lock", "Lock"),
    IconSpec("Regular", "magnifying-glass", "MagnifyingGlass"),
    IconSpec("Regular", "map-pin", "MapPin"),
    IconSpec("Regular", "medal", "Medal"),
    IconSpec("Regular", "minus", "Minus"),
    IconSpec("Regular", "pencil-simple", "PencilSimple"),
    IconSpec("Regular", "phone", "Phone"),
    IconSpec("Regular", "piggy-bank", "PiggyBank"),
    IconSpec("Regular", "plus", "Plus"),
    IconSpec("Regular", "qr-code", "QrCode"),
    IconSpec("Regular", "question", "Question"),
    IconSpec("Regular", "seal-check", "SealCheck"),
    IconSpec("Regular", "shield-check", "ShieldCheck"),
    IconSpec("Regular", "shopping-cart", "ShoppingCart"),
    IconSpec("Regular", "share-network", "ShareNetwork"),
    IconSpec("Regular", "sliders-horizontal", "SlidersHorizontal"),
    IconSpec("Regular", "sparkle", "Sparkle"),
    IconSpec("Regular", "star", "Star"),
    IconSpec("Regular", "tag", "Tag"),
    IconSpec("Regular", "trash", "Trash"),
    IconSpec("Regular", "truck", "Truck"),
    IconSpec("Regular", "user-circle", "UserCircle"),
    IconSpec("Regular", "users", "Users"),
    IconSpec("Regular", "wallet", "Wallet"),
    IconSpec("Regular", "warning-circle", "WarningCircle"),
    IconSpec("Regular", "x", "X"),
    IconSpec("Regular", "x-circle", "XCircle"),
    IconSpec("AutoMirrored", "arrow-left", "ArrowLeft", auto_mirror=True),
    IconSpec("AutoMirrored", "arrow-right", "ArrowRight", auto_mirror=True),
    IconSpec("AutoMirrored", "arrow-square-out", "ArrowSquareOut", auto_mirror=True),
    IconSpec("AutoMirrored", "caret-right", "CaretRight", auto_mirror=True),
    IconSpec("AutoMirrored", "paper-plane-tilt", "PaperPlaneTilt", auto_mirror=True),
    IconSpec("AutoMirrored", "sign-out", "SignOut", auto_mirror=True),
    IconSpec("AutoMirrored", "trend-down", "TrendDown", auto_mirror=True),
    IconSpec("AutoMirrored", "trend-up", "TrendUp", auto_mirror=True),
]


def local_name(tag: str) -> str:
    return tag.rsplit("}", 1)[-1]


def parse_float(value: str | None, default: float = 1.0) -> float:
    if value is None:
        return default
    return float(value)


def parse_viewbox(value: str) -> tuple[float, float]:
    parts = value.replace(",", " ").split()
    if len(parts) != 4:
        raise ValueError(f"Unsupported viewBox: {value}")
    return float(parts[2]), float(parts[3])


def format_float(value: float) -> str:
    if value.is_integer():
        return f"{int(value)}f"
    text = f"{value:.4f}".rstrip("0").rstrip(".")
    return f"{text}f"


def escape_kotlin(value: str) -> str:
    return value.replace("\\", "\\\\").replace("\"", "\\\"").replace("$", "\\$")


def collect_paths(node: ET.Element, inherited_alpha: float = 1.0) -> list[PathSpec]:
    node_alpha = inherited_alpha * parse_float(node.attrib.get("opacity"), 1.0)
    paths: list[PathSpec] = []

    if local_name(node.tag) == "path":
        if node.attrib.get("fill") == "none":
            return paths
        path_data = node.attrib.get("d")
        if path_data:
            paths.append(
                PathSpec(
                    data=path_data,
                    fill_type=node.attrib.get("fill-rule", "nonzero"),
                    fill_alpha=node_alpha * parse_float(node.attrib.get("fill-opacity"), 1.0),
                )
            )

    for child in list(node):
        paths.extend(collect_paths(child, node_alpha))

    return paths


def load_icon(source_root: Path, spec: IconSpec) -> tuple[float, float, list[PathSpec]]:
    icon_path = source_root / spec.weight / f"{spec.asset_name}.svg"
    if not icon_path.exists():
        raise FileNotFoundError(f"Missing icon asset: {icon_path}")

    root = ET.parse(icon_path).getroot()
    viewbox = root.attrib.get("viewBox")
    if viewbox is None:
        raise ValueError(f"Missing viewBox in {icon_path}")

    viewport_width, viewport_height = parse_viewbox(viewbox)
    paths = collect_paths(root)
    if not paths:
        raise ValueError(f"No paths found in {icon_path}")

    return viewport_width, viewport_height, paths


def build_icon_block(spec: IconSpec, viewport_width: float, viewport_height: float, paths: list[PathSpec]) -> str:
    cache_name = spec.kotlin_name[0].lower() + spec.kotlin_name[1:] + "Cache"
    lines = [
        f"        val {spec.kotlin_name}: ImageVector",
        "            get() {",
        f"                if ({cache_name} != null) {{",
        f"                    return {cache_name}!!",
        "                }",
        f"                {cache_name} = buildPhosphorIcon(",
        f"                    name = \"{spec.kotlin_name}\",",
        f"                    viewportWidth = {format_float(viewport_width)},",
        f"                    viewportHeight = {format_float(viewport_height)},",
        f"                    autoMirror = {'true' if spec.auto_mirror else 'false'},",
        "                ) {",
    ]

    for path in paths:
        fill_type = "EvenOdd" if path.fill_type.lower() == "evenodd" else "NonZero"
        lines.extend(
            [
                "                    addPath(",
                f"                        pathData = addPathNodes(\"{escape_kotlin(path.data)}\"),",
                f"                        pathFillType = PathFillType.{fill_type},",
                "                        fill = SolidColor(Color.Black),",
                (
                    f"                        fillAlpha = {format_float(path.fill_alpha)},"
                    if abs(path.fill_alpha - 1.0) > 1e-6
                    else ""
                ),
                "                    )",
            ]
        )

    lines.extend(
        [
            "                }",
            f"                return {cache_name}!!",
            "            }",
            "",
            f"        private var {cache_name}: ImageVector? = null",
        ]
    )

    return "\n".join(line for line in lines if line)


def render_group(source_root: Path, group_name: str) -> str:
    blocks = []
    for spec in ICON_SPECS:
        if spec.group_name != group_name:
            continue
        viewport_width, viewport_height, paths = load_icon(source_root, spec)
        blocks.append(build_icon_block(spec, viewport_width, viewport_height, paths))
    body = "\n\n".join(blocks)
    return f"""    object {group_name} {{
{body}
    }}"""


def render_file(source_root: Path) -> str:
    groups = ["Regular", "AutoMirrored"]
    rendered_groups = "\n\n".join(render_group(source_root, group) for group in groups)

    return f"""package com.habit.gold.core.designsystem.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

// Generated by scripts/generate_phosphor_icons.py from phosphor-icons/core assets.
internal object HabitGoldPhosphorIcons {{
{rendered_groups}
}}

private fun buildPhosphorIcon(
    name: String,
    viewportWidth: Float,
    viewportHeight: Float,
    autoMirror: Boolean = false,
    block: ImageVector.Builder.() -> Unit,
): ImageVector = ImageVector.Builder(
    name = name,
    defaultWidth = 24.dp,
    defaultHeight = 24.dp,
    viewportWidth = viewportWidth,
    viewportHeight = viewportHeight,
    autoMirror = autoMirror,
).apply(block).build()
"""


def main() -> None:
    parser = argparse.ArgumentParser(
        description="Generate a shared Compose Multiplatform Phosphor icon bundle from official SVG assets."
    )
    parser.add_argument(
        "--source-root",
        type=Path,
        default=DEFAULT_SOURCE_ROOT,
        help=f"Phosphor asset root. Defaults to {DEFAULT_SOURCE_ROOT}",
    )
    parser.add_argument(
        "--output",
        type=Path,
        default=DEFAULT_OUTPUT,
        help=f"Generated Kotlin file path. Defaults to {DEFAULT_OUTPUT}",
    )
    args = parser.parse_args()

    content = render_file(args.source_root)
    args.output.parent.mkdir(parents=True, exist_ok=True)
    args.output.write_text(content + "\n", encoding="utf-8")


if __name__ == "__main__":
    main()
