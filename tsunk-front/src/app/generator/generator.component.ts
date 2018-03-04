import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHandler } from '@angular/common/http';
import { HttpRequest } from '@angular/common/http';

import { InterceptorService } from '../service/interceptorService';

interface Morphemes{
  morphemes: Morpheme[]
}

interface Morpheme{
  surface: string,
  pos: string,
}

@Component({
  selector: 'app-generator',
  templateUrl: './generator.component.html',
  styleUrls: ['./generator.component.css']
})
export class GeneratorComponent implements OnInit {
  getMorphemeUrl = 'http://localhost:9000/api/word';
  morphemes: Morpheme[];
  randMorpheme: Morpheme;

  constructor(
    private httpClient: HttpClient,
    private interceptorService: InterceptorService,
    private httpHandler: HttpHandler,
  ) { }

  ngOnInit() {
  }

  getMorpheme(): void {
    this.httpClient.get<Morphemes>(this.getMorphemeUrl).subscribe(result => {
      this.morphemes = result.morphemes
    })
  }
}
