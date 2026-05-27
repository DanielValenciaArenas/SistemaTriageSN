/* PERSONA C */
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { IaService } from '../../ia.service';

/** PERSONA C — implementar RF-09 y RF-10 */
@Component({
  selector: 'app-panel-ia',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './panel-ia.component.html',
  styleUrl: './panel-ia.component.css'
})
export class PanelIaComponent {
  constructor(public iaService: IaService) {}
}
